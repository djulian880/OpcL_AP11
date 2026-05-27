package com.openclassrooms.micro_bed_availability.infra;


import com.openclassrooms.micro_bed_availability.domain.fetch.*;
import com.openclassrooms.micro_bed_availability.infra.event.AppointmentEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests d'intégration Spring pour BedFetchService.
 *
 * Le contexte Spring complet est chargé. Les dépendances externes
 * (repositories, publisher, distanceCalculator) sont mockées afin
 * d'éviter tout appel réseau ou accès à des fichiers OSM.
 */
@SpringBootTest
@ActiveProfiles("test")
@Tag("integration")
class BedFetchServiceIntegrationTest {

    @Autowired
    private BedFetchService bedFetchService;

    // @MockBean remplace le bean dans le contexte Spring ET évite le chargement OSM
    @MockitoBean
    private DistanceCalculatorService distanceCalculatorService;

    @MockitoBean
    private IHospitalRepository hospitalRepository;

    @MockitoBean
    private IAppointmentRepository appointmentRepository;

    @MockitoBean
    private IPublishEvent eventPublisher;

    @MockitoBean
    private ICoordinatesRepository coordinatesRepository;

    // ── Helpers ───────────────────────────────────────────────────────────────

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

    // ── Scénarios ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("[IT] Un lit est trouvé et un événement de réservation est publié")
    void integration_bedFound_eventPublished() throws InterruptedException {
        Hospital h = buildHospital("Hôpital Central", "10 avenue de la Santé", 5, 48.5, 7.75);

        Coordinates patientCoords = new Coordinates();
        patientCoords.setLatitude(48.48);
        patientCoords.setLongitude(7.73);

        when(hospitalRepository.getHospitals("cardio")).thenReturn(List.of(h));
        when(appointmentRepository.getAppointments(anyString(), eq("cardio")))
                .thenReturn(Collections.emptyList());
        when(coordinatesRepository.getCoordinates("5 rue du Général")).thenReturn(patientCoords);
        when(distanceCalculatorService.calculateDistance(any(), any())).thenReturn(3000.0);

        Bed result = bedFetchService
                .fetchFreeBedByNearestHospitalAndSpecialty("5 rue du Général", "cardio");

        Thread.sleep(300); // attente du thread de publication asynchrone

        assertThat(result).isNotNull();
        assertThat(result.getHospitalName()).isEqualTo("Hôpital Central");
        assertThat(result.getSpeciality()).isEqualTo("cardio");
        verify(eventPublisher, times(1)).publish(any(AppointmentEvent.class));
    }

    @Test
    @DisplayName("[IT] Aucun lit disponible → retourne null, aucun événement publié")
    void integration_noAvailability_returnsNull() {
        Hospital h = buildHospital("Hôpital Plein", "2 boulevard Sud", 2, 48.5, 7.75);

        Coordinates patientCoords = new Coordinates();
        patientCoords.setLatitude(48.48);
        patientCoords.setLongitude(7.73);

        when(hospitalRepository.getHospitals("cardio")).thenReturn(List.of(h));
        when(appointmentRepository.getAppointments(anyString(), eq("cardio")))
                .thenReturn(List.of(buildAppointment("Hôpital Plein"),
                        buildAppointment("Hôpital Plein")));
        when(coordinatesRepository.getCoordinates("5 rue du Général")).thenReturn(patientCoords);
        when(distanceCalculatorService.calculateDistance(any(), any())).thenReturn(3000.0);

        Bed result = bedFetchService
                .fetchFreeBedByNearestHospitalAndSpecialty("5 rue du Général", "cardio");

        assertThat(result).isNull();
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("[IT] Sélection du plus proche parmi plusieurs hôpitaux partiellement complets")
    void integration_selectsNearestFreeHospital() throws InterruptedException {
        Hospital full = buildHospital("Hôpital Complet", "1 rue A", 2, 48.5,  7.75);
        Hospital near = buildHospital("Hôpital Proche",  "2 rue B", 3, 48.49, 7.74);
        Hospital far  = buildHospital("Hôpital Loin",    "3 rue C", 5, 49.0,  8.0);

        Coordinates patientCoords = new Coordinates();
        patientCoords.setLatitude(48.48);
        patientCoords.setLongitude(7.73);

        when(hospitalRepository.getHospitals("ortho"))
                .thenReturn(List.of(full, near, far));
        when(appointmentRepository.getAppointments(anyString(), eq("ortho")))
                .thenReturn(List.of(buildAppointment("Hôpital Complet"),
                        buildAppointment("Hôpital Complet")));
        when(coordinatesRepository.getCoordinates("7 rue Test")).thenReturn(patientCoords);

        when(distanceCalculatorService.calculateDistance(eq(patientCoords), eq(full.getCoordinates())))
                .thenReturn(500.0);
        when(distanceCalculatorService.calculateDistance(eq(patientCoords), eq(near.getCoordinates())))
                .thenReturn(1200.0);
        when(distanceCalculatorService.calculateDistance(eq(patientCoords), eq(far.getCoordinates())))
                .thenReturn(90000.0);

        Bed result = bedFetchService
                .fetchFreeBedByNearestHospitalAndSpecialty("7 rue Test", "ortho");

        Thread.sleep(300);

        assertThat(result).isNotNull();
        // "Hôpital Complet" est le plus proche mais plein (2/2) → doit choisir "Hôpital Proche"
        assertThat(result.getHospitalName()).isEqualTo("Hôpital Proche");
        verify(eventPublisher, times(1)).publish(any(AppointmentEvent.class));
    }

    @Test
    @DisplayName("[IT] Liste d'hôpitaux vide → retourne null")
    void integration_emptyHospitalList_returnsNull() {
        Coordinates patientCoords = new Coordinates();
        patientCoords.setLatitude(48.48);
        patientCoords.setLongitude(7.73);

        when(hospitalRepository.getHospitals("neurologie")).thenReturn(Collections.emptyList());
        when(appointmentRepository.getAppointments(anyString(), eq("neurologie")))
                .thenReturn(Collections.emptyList());
        when(coordinatesRepository.getCoordinates("1 rue Vide")).thenReturn(patientCoords);

        Bed result = bedFetchService
                .fetchFreeBedByNearestHospitalAndSpecialty("1 rue Vide", "neurologie");

        assertThat(result).isNull();
        verify(eventPublisher, never()).publish(any());
    }
}