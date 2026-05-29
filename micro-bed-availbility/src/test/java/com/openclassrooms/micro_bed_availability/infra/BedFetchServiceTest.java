package com.openclassrooms.micro_bed_availability.infra;

import com.openclassrooms.micro_bed_availability.domain.fetch.*;
import com.openclassrooms.micro_bed_availability.infra.event.AppointmentEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class BedFetchServiceTest {

    @Mock
    private IHospitalRepository hospitalRepository;

    @Mock
    private IAppointmentRepository appointmentRepository;

    @Mock
    private IPublishEvent eventPublisher;

    @Mock
    private ICoordinatesRepository coordinatesRepository;

    @Mock
    private DistanceCalculatorService distanceCalculatorService;

    // Pas @InjectMocks : on construit manuellement pour contrôler l'injection du champ @Autowired
    private BedFetchService bedFetchService;

    @BeforeEach
    void setUp() {
        bedFetchService = new BedFetchService(
                hospitalRepository,
                appointmentRepository,
                eventPublisher,
                coordinatesRepository
        );
        // Injection manuelle du champ @Autowired ignoré par @InjectMocks
        org.springframework.test.util.ReflectionTestUtils.setField(
                bedFetchService, "distanceCalculatorService", distanceCalculatorService);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Hospital buildHospital(String name, String address, int beds, double lat, double lon) {
        Coordinates coords = new Coordinates();
        coords.setLatitude(lat);
        coords.setLongitude(lon);

        Hospital h = new Hospital();
        h.setName(name);
        h.setAddress(address);
        h.setTotalNumberOfBeds(beds);
        h.setCoordinates(coords);
        return h;
    }

    private Appointment buildAppointment(String hospitalName) {
        Appointment a = new Appointment();
        a.setHospital(hospitalName);
        return a;
    }

    private Coordinates buildCoordinates(double lat, double lon) {
        Coordinates c = new Coordinates();
        c.setLatitude(lat);
        c.setLongitude(lon);
        return c;
    }

    // ── returnCurrentDate ────────────────────────────────────────────────────

    @Test
    @DisplayName("returnCurrentDate() retourne une date au format yyyy-MM-dd")
    void returnCurrentDate_formatIsCorrect() {
        String date = bedFetchService.returnCurrentDate();
        assertThat(date).matches("\\d{4}-\\d{2}-\\d{2}");
    }

    // ── orderAppointments ────────────────────────────────────────────────────

    @Test
    @DisplayName("orderAppointments() compte correctement les rendez-vous par hôpital")
    void orderAppointments_countsCorrectly() {
        Hospital h1 = buildHospital("HopA", "addr1", 10, 0, 0);
        Hospital h2 = buildHospital("HopB", "addr2", 5, 0, 0);

        List<Appointment> appointments = List.of(
                buildAppointment("HopA"),
                buildAppointment("HopA"),
                buildAppointment("HopB")
        );

        Map<String, Integer> result = bedFetchService.orderAppointments(List.of(h1, h2), appointments);

        assertThat(result).containsEntry("HopA", 2).containsEntry("HopB", 1);
    }

    @Test
    @DisplayName("orderAppointments() retourne une map vide si aucun rendez-vous")
    void orderAppointments_emptyAppointments() {
        Hospital h1 = buildHospital("HopA", "addr1", 10, 0, 0);
        Map<String, Integer> result =
                bedFetchService.orderAppointments(List.of(h1), Collections.emptyList());
        assertThat(result).isEmpty();
    }

    // ── findNearestAndFree ────────────────────────────────────────────────────

    @Test
    @DisplayName("findNearestAndFree() retourne l'hôpital le plus proche sans rendez-vous")
    void findNearestAndFree_returnsNearestWithNoAppointments() {
        Hospital near = buildHospital("HopNear", "addr1", 5, 0, 0);
        Hospital far  = buildHospital("HopFar",  "addr2", 5, 0, 0);

        TreeMap<Double, Hospital> distMap = new TreeMap<>();
        distMap.put(1.0, near);
        distMap.put(10.0, far);

        Hospital result = bedFetchService.findNearestAndFree(distMap, new HashMap<>());

        assertThat(result).isEqualTo(near);
    }

    @Test
    @DisplayName("findNearestAndFree() saute l'hôpital le plus proche si tous ses lits sont pris")
    void findNearestAndFree_skipsFullHospital() {
        Hospital near = buildHospital("HopNear", "addr1", 2, 0, 0);
        Hospital far  = buildHospital("HopFar",  "addr2", 5, 0, 0);

        TreeMap<Double, Hospital> distMap = new TreeMap<>();
        distMap.put(1.0, near);
        distMap.put(10.0, far);

        Map<String, Integer> appointments = new HashMap<>();
        appointments.put("HopNear", 2); // 2 lits occupés sur 2 → 0 libre

        Hospital result = bedFetchService.findNearestAndFree(distMap, appointments);

        assertThat(result).isEqualTo(far);
    }

    @Test
    @DisplayName("findNearestAndFree() retourne null si tous les hôpitaux sont complets")
    void findNearestAndFree_allFull_returnsNull() {
        Hospital h1 = buildHospital("HopA", "addr1", 1, 0, 0);
        Hospital h2 = buildHospital("HopB", "addr2", 1, 0, 0);

        TreeMap<Double, Hospital> distMap = new TreeMap<>();
        distMap.put(1.0, h1);
        distMap.put(2.0, h2);

        Map<String, Integer> appointments = new HashMap<>();
        appointments.put("HopA", 1);
        appointments.put("HopB", 1);

        assertThat(bedFetchService.findNearestAndFree(distMap, appointments)).isNull();
    }

    @Test
    @DisplayName("findNearestAndFree() retourne null si la liste d'hôpitaux est vide")
    void findNearestAndFree_emptyList_returnsNull() {
        assertThat(bedFetchService.findNearestAndFree(new TreeMap<>(), new HashMap<>())).isNull();
    }

    // ── calcDistance ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("calcDistance() trie les hôpitaux par distance croissante")
    void calcDistance_sortsByDistance() {
        Coordinates start = buildCoordinates(48.0, 2.0);
        Hospital near = buildHospital("HopNear", "addr1", 5, 48.1, 2.1);
        Hospital far  = buildHospital("HopFar",  "addr2", 5, 50.0, 5.0);

        when(distanceCalculatorService.calculateDistance(eq(start), eq(near.getCoordinates())))
                .thenReturn(1000.0);
        when(distanceCalculatorService.calculateDistance(eq(start), eq(far.getCoordinates())))
                .thenReturn(50000.0);

        TreeMap<Double, Hospital> result = bedFetchService.calcDistance(List.of(near, far), start);

        assertThat(result.firstEntry().getValue()).isEqualTo(near);
        assertThat(result.lastEntry().getValue()).isEqualTo(far);
    }

    // ── publishBooking ────────────────────────────────────────────────────────

    @Test
    @DisplayName("publishBooking() publie un AppointmentEvent avec les bons champs")
    void publishBooking_publishesCorrectEvent() {
        Bed bed = new Bed();
        bed.setHospitalName("HopA");
        bed.setHospitalAddress("1 rue Test");
        bed.setSpeciality("cardio");

        bedFetchService.publishBooking("cardio", bed, "2024-01-15");

        ArgumentCaptor<AppointmentEvent> captor = ArgumentCaptor.forClass(AppointmentEvent.class);
        verify(eventPublisher, times(1)).publish(captor.capture());

        AppointmentEvent event = captor.getValue();
        assertThat(event.hospitalName()).isEqualTo("HopA");
        assertThat(event.speciality()).isEqualTo("cardio");
        assertThat(event.status()).isEqualTo("booking");
        assertThat(event.date()).isEqualTo("2024-01-15");
    }

    // ── fetchFreeBedByNearestHospitalAndSpecialty ─────────────────────────────

    @Test
    @DisplayName("fetchFreeBedByNearestHospitalAndSpecialty() retourne un lit quand un hôpital est disponible")
    void fetchFreeBed_returnsBedWhenHospitalAvailable() throws InterruptedException {
        Hospital h = buildHospital("HopA", "1 rue OK", 5, 48.5, 7.7);
        Coordinates patientCoords = buildCoordinates(48.4, 7.6);

        when(hospitalRepository.getHospitals("cardio")).thenReturn(List.of(h));
        when(appointmentRepository.getAppointments(anyString(), eq("cardio")))
                .thenReturn(Collections.emptyList());
        when(coordinatesRepository.getCoordinates("12 rue Molière")).thenReturn(patientCoords);
        when(distanceCalculatorService.calculateDistance(any(), any())).thenReturn(5000.0);

        Bed result = bedFetchService
                .fetchFreeBedByNearestHospitalAndSpecialty("12 rue Molière", "cardio");

        Thread.sleep(300); // attente du thread de publication asynchrone

        assertThat(result).isNotNull();
        assertThat(result.getHospitalName()).isEqualTo("HopA");
        assertThat(result.getHospitalAddress()).isEqualTo("1 rue OK");
        assertThat(result.getSpeciality()).isEqualTo("cardio");
        verify(eventPublisher, times(1)).publish(any(AppointmentEvent.class));
    }

    @Test
    @DisplayName("fetchFreeBedByNearestHospitalAndSpecialty() retourne null si aucun lit disponible")
    void fetchFreeBed_returnsNullWhenNoAvailability() {
        Hospital h = buildHospital("HopFull", "2 rue Plein", 1, 48.5, 7.7);
        Coordinates patientCoords = buildCoordinates(48.4, 7.6);

        when(hospitalRepository.getHospitals("cardio")).thenReturn(List.of(h));
        when(appointmentRepository.getAppointments(anyString(), eq("cardio")))
                .thenReturn(List.of(buildAppointment("HopFull")));
        when(coordinatesRepository.getCoordinates("12 rue Molière")).thenReturn(patientCoords);
        when(distanceCalculatorService.calculateDistance(any(), any())).thenReturn(5000.0);

        Bed result = bedFetchService
                .fetchFreeBedByNearestHospitalAndSpecialty("12 rue Molière", "cardio");

        assertThat(result).isNull();
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("fetchFreeBedByNearestHospitalAndSpecialty() choisit le plus proche parmi plusieurs hôpitaux")
    void fetchFreeBed_choosesNearestHospital() throws InterruptedException {
        Hospital near = buildHospital("HopNear", "addr near", 5, 48.41, 7.61);
        Hospital far  = buildHospital("HopFar",  "addr far",  5, 49.0,  8.0);
        Coordinates patientCoords = buildCoordinates(48.4, 7.6);

        when(hospitalRepository.getHospitals("ortho")).thenReturn(List.of(near, far));
        when(appointmentRepository.getAppointments(anyString(), eq("ortho")))
                .thenReturn(Collections.emptyList());
        when(coordinatesRepository.getCoordinates("3 rue Test")).thenReturn(patientCoords);
        when(distanceCalculatorService.calculateDistance(eq(patientCoords), eq(near.getCoordinates())))
                .thenReturn(1000.0);
        when(distanceCalculatorService.calculateDistance(eq(patientCoords), eq(far.getCoordinates())))
                .thenReturn(80000.0);

        Bed result = bedFetchService
                .fetchFreeBedByNearestHospitalAndSpecialty("3 rue Test", "ortho");

        Thread.sleep(300);

        assertThat(result).isNotNull();
        assertThat(result.getHospitalName()).isEqualTo("HopNear");
    }
}