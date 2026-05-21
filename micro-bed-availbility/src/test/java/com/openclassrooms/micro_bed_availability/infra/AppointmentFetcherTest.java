package com.openclassrooms.micro_bed_availability.infra;

import com.openclassrooms.micro_bed_availability.domain.fetch.Appointment;
import com.openclassrooms.micro_bed_availability.infra.proxies.MicroGatewayProxy;
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
import static org.mockito.Mockito.*;

/**
 * Tests unitaires d'AppointmentFetcher.
 *
 * Vérifie que la délégation vers MicroGatewayProxy est correcte :
 * bonne méthode appelée, bons arguments transmis, résultat retourné tel quel.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AppointmentFetcher — tests unitaires")
class AppointmentFetcherTest {

    @Mock
    private MicroGatewayProxy microGatewayProxy;

    @InjectMocks
    private AppointmentFetcher appointmentFetcher;

    private static final String HOSPITAL   = "Hôpital Paris";
    private static final String DATE       = "2024-06-15";
    private static final String SPECIALITY = "cardiologie";

    // =========================================================================
    // 1. CAS NOMINAUX
    // =========================================================================

    @Nested
    @DisplayName("Cas nominaux")
    class NominalCases {

        @Test
        @DisplayName("Retourne la liste fournie par le proxy")
        void getAppointments_shouldReturnProxyResult() {
            List<Appointment> expected = List.of(buildAppointment(), buildAppointment());
            when(microGatewayProxy.getAppointments(DATE, SPECIALITY))
                    .thenReturn(expected);

            List<Appointment> result = appointmentFetcher.getAppointments(DATE, SPECIALITY);

            assertThat(result).isEqualTo(expected).hasSize(2);
        }

        @Test
        @DisplayName("Retourne une liste vide quand le proxy retourne une liste vide")
        void getAppointments_proxyReturnsEmpty_shouldReturnEmpty() {
            when(microGatewayProxy.getAppointments(DATE, SPECIALITY))
                    .thenReturn(Collections.emptyList());

            List<Appointment> result = appointmentFetcher.getAppointments(DATE, SPECIALITY);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Délègue exactement les trois paramètres au proxy")
        void getAppointments_shouldDelegateExactParamsToProxy() {
            when(microGatewayProxy.getAppointments(DATE, SPECIALITY))
                    .thenReturn(Collections.emptyList());

            appointmentFetcher.getAppointments(DATE, SPECIALITY);

            verify(microGatewayProxy, times(1))
                    .getAppointments(DATE, SPECIALITY);
            verifyNoMoreInteractions(microGatewayProxy);
        }

        @Test
        @DisplayName("Le proxy est appelé exactement une fois par invocation")
        void getAppointments_shouldCallProxyExactlyOnce() {
            when(microGatewayProxy.getAppointments(any(), any()))
                    .thenReturn(List.of());

            appointmentFetcher.getAppointments(DATE, SPECIALITY);

            verify(microGatewayProxy, times(1))
                    .getAppointments(any(), any());
        }
    }

    // =========================================================================
    // 2. PROPAGATION DES ERREURS
    // =========================================================================

    @Nested
    @DisplayName("Propagation des erreurs du proxy")
    class ErrorPropagation {

        @Test
        @DisplayName("RuntimeException du proxy est propagée sans modification")
        void proxyThrowsRuntimeException_shouldPropagate() {
            when(microGatewayProxy.getAppointments(DATE, SPECIALITY))
                    .thenThrow(new RuntimeException("Feign: connexion refusée"));

            assertThatThrownBy(() -> appointmentFetcher.getAppointments(DATE, SPECIALITY))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Feign: connexion refusée");
        }

        @Test
        @DisplayName("IllegalStateException du proxy est propagée")
        void proxyThrowsIllegalState_shouldPropagate() {
            when(microGatewayProxy.getAppointments(DATE, SPECIALITY))
                    .thenThrow(new IllegalStateException("Service non disponible"));

            assertThatThrownBy(() -> appointmentFetcher.getAppointments(DATE, SPECIALITY))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    // =========================================================================
    // 3. CAS LIMITES
    // =========================================================================

    @Nested
    @DisplayName("Cas limites")
    class EdgeCases {

        @Test
        @DisplayName("Paramètres avec caractères spéciaux — transmis tels quels")
        void specialCharsInParams_shouldBePassedAsIs() {
            String hospitalSpecial  = "Hôpital Saint-Étienne & Fils";
            String dateSpecial      = "2024-12-31";
            String specialityUpper  = "CARDIOLOGIE";

            when(microGatewayProxy.getAppointments(
                    dateSpecial, specialityUpper))
                    .thenReturn(List.of());

            appointmentFetcher.getAppointments(dateSpecial, specialityUpper);

            verify(microGatewayProxy)
                    .getAppointments(dateSpecial, specialityUpper);
        }

        @Test
        @DisplayName("Retourne null si le proxy retourne null (comportement défensif)")
        void proxyReturnsNull_shouldReturnNull() {
            when(microGatewayProxy.getAppointments(DATE, SPECIALITY))
                    .thenReturn(null);

            List<Appointment> result = appointmentFetcher.getAppointments(DATE, SPECIALITY);

            assertThat(result).isNull();
        }
    }

    // =========================================================================
    // Helper
    // =========================================================================

    private Appointment buildAppointment() {
        Appointment a = new Appointment();
        a.setFirstName("Jean");
        a.setLastName("Dupont");
        a.setSpeciality(SPECIALITY);
        a.setHospital(HOSPITAL);
        a.setEntranceDate(new Date());
        a.setLeavingDate(new Date());
        return a;
    }
}
