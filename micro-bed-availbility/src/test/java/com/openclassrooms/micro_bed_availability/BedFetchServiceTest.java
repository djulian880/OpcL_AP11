package com.openclassrooms.micro_bed_availability;

import com.openclassrooms.micro_bed_availability.domain.fetch.Appointment;
import com.openclassrooms.micro_bed_availability.domain.fetch.Hospital;
import com.openclassrooms.micro_bed_availability.domain.fetch.IAppointmentRepository;
import com.openclassrooms.micro_bed_availability.domain.fetch.IHospitalRepository;
import com.openclassrooms.micro_bed_availability.domain.fetch.DistanceCalculatorService;
import com.openclassrooms.micro_bed_availability.domain.fetch.BedFetchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires de BedFetchService.
 *
 * Stratégie : toutes les dépendances (IHospitalRepository, IAppointmentRepository,
 * DistanceCalculatorService) sont mockées afin d'isoler la logique métier pure.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BedFetchService — tests unitaires")
class BedFetchServiceTest {

    @Mock private IHospitalRepository hospitalRepository;
    @Mock private IAppointmentRepository appointmentRepository;
    @Mock private DistanceCalculatorService distanceCalculatorService;

    @InjectMocks
    private BedFetchService bedFetchService;

    private Hospital hospitalParis;
    private Hospital hospitalLyon;
    private static final String SPECIALITY     = "cardiologie";
    private static final String CALLER_ADDRESS = "10 rue du Test, Strasbourg";

    @BeforeEach
    void setUp() {
        hospitalParis = buildHospital("Hôpital Paris", "1 rue de la Paix, Paris", 10);
        hospitalLyon  = buildHospital("Hôpital Lyon",  "5 avenue des Roses, Lyon",  5);
    }

    // =========================================================================
    // 1. CAS NOMINAUX
    // =========================================================================

    /*
    @Nested
    @DisplayName("Cas nominaux")
    class NominalCases {

        @Test
        @DisplayName("Retourne le lit de l'hôpital libre le plus proche")
        void shouldReturnNearestFreeHospital() {
            when(hospitalRepository.getHospitals(SPECIALITY))
                    .thenReturn(List.of(hospitalParis, hospitalLyon));
            when(appointmentRepository.getAppointments(eq("Hôpital Paris"), anyString(), eq(SPECIALITY)))
                    .thenReturn(List.of());
            when(appointmentRepository.getAppointments(eq("Hôpital Lyon"), anyString(), eq(SPECIALITY)))
                    .thenReturn(nAppointments(3));
            when(distanceCalculatorService.calculateDistance(anyString(), eq("1 rue de la Paix, Paris")))
                    .thenReturn(500.0);
            when(distanceCalculatorService.calculateDistance(anyString(), eq("5 avenue des Roses, Lyon")))
                    .thenReturn(45000.0);

            Bed result = bedFetchService.fetchFreeBedByNearestHospitalAndSpecialty(CALLER_ADDRESS, SPECIALITY);

            assertThat(result).isNotNull();
            assertThat(result.getHospitalName()).isEqualTo("Hôpital Paris");
            assertThat(result.getHospitalAddress()).isEqualTo("1 rue de la Paix, Paris");
            assertThat(result.getSpeciality()).isEqualTo(SPECIALITY);
        }

        @Test
        @DisplayName("Retourne Lyon quand il est plus proche que Paris")
        void shouldReturnLyonWhenCloser() {
            when(hospitalRepository.getHospitals(SPECIALITY))
                    .thenReturn(List.of(hospitalParis, hospitalLyon));
            when(appointmentRepository.getAppointments(anyString(), anyString(), anyString()))
                    .thenReturn(List.of());
            when(distanceCalculatorService.calculateDistance(anyString(), eq("1 rue de la Paix, Paris")))
                    .thenReturn(80000.0);
            when(distanceCalculatorService.calculateDistance(anyString(), eq("5 avenue des Roses, Lyon")))
                    .thenReturn(1200.0);

            Bed result = bedFetchService.fetchFreeBedByNearestHospitalAndSpecialty(CALLER_ADDRESS, SPECIALITY);

            assertThat(result.getHospitalName()).isEqualTo("Hôpital Lyon");
        }

        @Test
        @DisplayName("Un seul hôpital avec lits libres — retourné directement")
        void singleHospitalWithFreeBeds_shouldBeReturned() {
            when(hospitalRepository.getHospitals(SPECIALITY)).thenReturn(List.of(hospitalParis));
            when(appointmentRepository.getAppointments(anyString(), anyString(), anyString()))
                    .thenReturn(List.of());
            when(distanceCalculatorService.calculateDistance(anyString(), anyString()))
                    .thenReturn(1000.0);

            Bed result = bedFetchService.fetchFreeBedByNearestHospitalAndSpecialty(CALLER_ADDRESS, SPECIALITY);

            assertThat(result.getHospitalName()).isEqualTo("Hôpital Paris");
        }

        @Test
        @DisplayName("La date transmise au repository est au format yyyy-MM-dd")
        void shouldPassFormattedDateToAppointmentRepository() {
            when(hospitalRepository.getHospitals(SPECIALITY)).thenReturn(List.of(hospitalParis));
            when(appointmentRepository.getAppointments(anyString(), anyString(), anyString()))
                    .thenReturn(List.of());
            when(distanceCalculatorService.calculateDistance(anyString(), anyString()))
                    .thenReturn(1000.0);

            bedFetchService.fetchFreeBedByNearestHospitalAndSpecialty(CALLER_ADDRESS, SPECIALITY);

            verify(appointmentRepository).getAppointments(
                    eq("Hôpital Paris"),
                    matches("\\d{4}-\\d{2}-\\d{2}"),
                    eq(SPECIALITY)
            );
        }

        @Test
        @DisplayName("Le repository de rdv est interrogé pour chaque hôpital")
        void shouldQueryAppointmentsForEachHospital() {
            when(hospitalRepository.getHospitals(SPECIALITY))
                    .thenReturn(List.of(hospitalParis, hospitalLyon));
            when(appointmentRepository.getAppointments(anyString(), anyString(), anyString()))
                    .thenReturn(List.of());
            when(distanceCalculatorService.calculateDistance(anyString(), anyString()))
                    .thenReturn(1000.0);

            bedFetchService.fetchFreeBedByNearestHospitalAndSpecialty(CALLER_ADDRESS, SPECIALITY);

            verify(appointmentRepository, times(2))
                    .getAppointments(anyString(), anyString(), eq(SPECIALITY));
        }
    }*/

    // =========================================================================
    // 2. FILTRAGE DES HÔPITAUX COMPLETS
    // =========================================================================

    @Nested
    @DisplayName("Filtrage des hôpitaux complets")
    class FullHospitalFiltering {

        @Test
        @DisplayName("Un hôpital complet n'est jamais soumis au calcul de distance")
        void fullHospital_shouldNotBePassedToDistanceCalculator() {
            when(hospitalRepository.getHospitals(SPECIALITY)).thenReturn(List.of(hospitalParis));
            when(appointmentRepository.getAppointments(anyString(), anyString()))
                    .thenReturn(nAppointments(10)); // 10 lits, 10 rdv

            assertThatThrownBy(() ->
                    bedFetchService.fetchFreeBedByNearestHospitalAndSpecialty(CALLER_ADDRESS, SPECIALITY))
                    .isInstanceOf(Exception.class);

            verifyNoInteractions(distanceCalculatorService);
        }

        /*
        @Test
        @DisplayName("Ignore Paris (complet) et retourne Lyon (libre)")
        void shouldIgnoreFullHospitalAndReturnFreeOne() {
            when(hospitalRepository.getHospitals(SPECIALITY))
                    .thenReturn(List.of(hospitalParis, hospitalLyon));
            when(appointmentRepository.getAppointments(eq("Hôpital Paris"), anyString(), eq(SPECIALITY)))
                    .thenReturn(nAppointments(10));
            when(appointmentRepository.getAppointments(eq("Hôpital Lyon"), anyString(), eq(SPECIALITY)))
                    .thenReturn(nAppointments(2));
            when(distanceCalculatorService.calculateDistance(anyString(), eq("5 avenue des Roses, Lyon")))
                    .thenReturn(3000.0);

            Bed result = bedFetchService.fetchFreeBedByNearestHospitalAndSpecialty(CALLER_ADDRESS, SPECIALITY);

            assertThat(result.getHospitalName()).isEqualTo("Hôpital Lyon");
            verify(distanceCalculatorService, never())
                    .calculateDistance(anyString(), eq("1 rue de la Paix, Paris"));
        }*/

       /* @Test
        @DisplayName("Hôpital avec exactement 1 lit libre est inclus (valeur limite)")
        void hospitalWithExactlyOneFreeBed_shouldBeIncluded() {
            // 5 lits, 4 rdv → 1 lit libre
            when(hospitalRepository.getHospitals(SPECIALITY)).thenReturn(List.of(hospitalLyon));
            when(appointmentRepository.getAppointments(anyString(), anyString(), anyString()))
                    .thenReturn(nAppointments(4));
            when(distanceCalculatorService.calculateDistance(anyString(), anyString()))
                    .thenReturn(1000.0);

            Bed result = bedFetchService.fetchFreeBedByNearestHospitalAndSpecialty(CALLER_ADDRESS, SPECIALITY);

            assertThat(result.getHospitalName()).isEqualTo("Hôpital Lyon");
        }
*/
        @Test
        @DisplayName("Hôpital avec lits = rdv est exclu (valeur limite exacte)")
        void hospitalAtExactCapacity_shouldBeExcluded() {
            // 5 lits, 5 rdv → complet
            when(hospitalRepository.getHospitals(SPECIALITY)).thenReturn(List.of(hospitalLyon));
            when(appointmentRepository.getAppointments( anyString(), anyString()))
                    .thenReturn(nAppointments(5));

            assertThatThrownBy(() ->
                    bedFetchService.fetchFreeBedByNearestHospitalAndSpecialty(CALLER_ADDRESS, SPECIALITY))
                    .isInstanceOf(Exception.class);
        }
    }

    // =========================================================================
    // 3. CAS LIMITES / ERREURS
    // =========================================================================

    @Nested
    @DisplayName("Cas limites et erreurs")
    class EdgeCases {

        /*
        @Test
        @DisplayName("Aucun hôpital pour la spécialité → NoSuchElementException")
        void noHospitalsForSpeciality_shouldThrow() {
            when(hospitalRepository.getHospitals("neurologie"))
                    .thenReturn(Collections.emptyList());

            assertThatThrownBy(() ->
                    bedFetchService.fetchFreeBedByNearestHospitalAndSpecialty(CALLER_ADDRESS, "neurologie"))
                    .isInstanceOf(NoSuchElementException.class);

            verifyNoInteractions(appointmentRepository);
            verifyNoInteractions(distanceCalculatorService);
        }*/

        /*
        @Test
        @DisplayName("Tous les hôpitaux complets → NoSuchElementException")
        void allHospitalsFull_shouldThrow() {
            when(hospitalRepository.getHospitals(SPECIALITY))
                    .thenReturn(List.of(hospitalParis, hospitalLyon));
            when(appointmentRepository.getAppointments(eq("Hôpital Paris"), anyString(), eq(SPECIALITY)))
                    .thenReturn(nAppointments(10));
            when(appointmentRepository.getAppointments(eq("Hôpital Lyon"), anyString(), eq(SPECIALITY)))
                    .thenReturn(nAppointments(5));

            assertThatThrownBy(() ->
                    bedFetchService.fetchFreeBedByNearestHospitalAndSpecialty(CALLER_ADDRESS, SPECIALITY))
                    .isInstanceOf(NoSuchElementException.class);

            verifyNoInteractions(distanceCalculatorService);
        }*/
/*
        @Test
        @DisplayName("Le repository d'hôpitaux lance une RuntimeException → propagée")
        void hospitalRepositoryThrows_shouldPropagate() {
            when(hospitalRepository.getHospitals(SPECIALITY))
                    .thenThrow(new RuntimeException("Service indisponible"));

            assertThatThrownBy(() ->
                    bedFetchService.fetchFreeBedByNearestHospitalAndSpecialty(CALLER_ADDRESS, SPECIALITY))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Service indisponible");
        }
*/
        /*
        @Test
        @DisplayName("Le repository de rdv lance une RuntimeException → propagée")
        void appointmentRepositoryThrows_shouldPropagate() {
            when(hospitalRepository.getHospitals(SPECIALITY)).thenReturn(List.of(hospitalParis));
            when(appointmentRepository.getAppointments( anyString(), anyString()))
                    .thenThrow(new RuntimeException("Timeout Feign"));

            assertThatThrownBy(() ->
                    bedFetchService.fetchFreeBedByNearestHospitalAndSpecialty(CALLER_ADDRESS, SPECIALITY))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Timeout Feign");
        }*/
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private Hospital buildHospital(String name, String address, int beds) {
        Hospital h = new Hospital();
        h.setName(name);
        h.setAddress(address);
        h.setTotalNumberOfBeds(beds);
        return h;
    }

    private List<Appointment> nAppointments(int n) {
        return Collections.nCopies(n, buildAppointment());
    }

    private Appointment buildAppointment() {
        Appointment a = new Appointment();
        a.setFirstName("Jean");
        a.setLastName("Dupont");
        a.setSpeciality(SPECIALITY);
        a.setHospital("Hôpital Paris");
        a.setEntranceDate(new Date());
        a.setLeavingDate(new Date());
        return a;
    }
}
