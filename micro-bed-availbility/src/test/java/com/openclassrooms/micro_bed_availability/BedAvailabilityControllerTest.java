package com.openclassrooms.micro_bed_availability;

import com.openclassrooms.micro_bed_availability.controller.BedAvailabilityController;
import com.openclassrooms.micro_bed_availability.domain.fetch.Bed;
import com.openclassrooms.micro_bed_availability.domain.fetch.BedFetchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires du controller REST BedAvailabilityController.
 *
 * Utilise MockMvc en mode standalone (sans contexte Spring complet)
 * pour tester uniquement la couche HTTP : mapping, paramètres, sérialisation JSON.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BedAvailabilityController — tests unitaires")
@WithMockUser
class BedAvailabilityControllerTest {

    @Mock
    private BedFetchService bedFetchService;

    @InjectMocks
    private BedAvailabilityController controller;

    private MockMvc mockMvc;

    private static final String URL          = "/beds";
    private static final String ADDRESS      = "10 rue du Test, Strasbourg";
    private static final String SPECIALITY   = "cardiologie";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // =========================================================================
    // 1. CAS NOMINAUX
    // =========================================================================

    @Nested
    @DisplayName("Cas nominaux")
    class NominalCases {

        @Test
        @DisplayName("GET /beds retourne 200 avec le JSON du lit trouvé")
        void getFreeBed_shouldReturn200WithBedJson() throws Exception {
            Bed bed = buildBed("Hôpital Paris", "1 rue de la Paix, Paris", SPECIALITY);
            when(bedFetchService.fetchFreeBedByNearestHospitalAndSpecialty(ADDRESS, SPECIALITY))
                    .thenReturn(bed);

            mockMvc.perform(get(URL)
                            .param("address", ADDRESS)
                            .param("speciality", SPECIALITY)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.hospitalName").value("Hôpital Paris"))
                    .andExpect(jsonPath("$.hospitalAddress").value("1 rue de la Paix, Paris"))
                    .andExpect(jsonPath("$.speciality").value(SPECIALITY));
        }

        @Test
        @DisplayName("GET /beds transmet exactement les paramètres au service")
        void getFreeBed_shouldDelegateParamsToService() throws Exception {
            Bed bed = buildBed("Hôpital Lyon", "5 avenue des Roses, Lyon", SPECIALITY);
            when(bedFetchService.fetchFreeBedByNearestHospitalAndSpecialty(ADDRESS, SPECIALITY))
                    .thenReturn(bed);

            mockMvc.perform(get(URL)
                    .param("address", ADDRESS)
                    .param("speciality", SPECIALITY));

            verify(bedFetchService, times(1))
                    .fetchFreeBedByNearestHospitalAndSpecialty(eq(ADDRESS), eq(SPECIALITY));
        }

        @Test
        @DisplayName("GET /beds avec une spécialité différente — paramètre bien transmis")
        void getFreeBed_differentSpeciality_shouldPassCorrectly() throws Exception {
            String neuro = "neurologie";
            Bed bed = buildBed("Hôpital Est", "10 avenue de l'Est, Paris", neuro);
            when(bedFetchService.fetchFreeBedByNearestHospitalAndSpecialty(ADDRESS, neuro))
                    .thenReturn(bed);

            mockMvc.perform(get(URL)
                            .param("address", ADDRESS)
                            .param("speciality", neuro))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.speciality").value(neuro));

            verify(bedFetchService).fetchFreeBedByNearestHospitalAndSpecialty(ADDRESS, neuro);
        }
    }

    // =========================================================================
    // 2. PARAMÈTRES MANQUANTS
    // =========================================================================

    @Nested
    @DisplayName("Paramètres manquants ou invalides")
    class MissingParams {

        @Test
        @DisplayName("GET /beds sans 'address' retourne 400")
        void missingAddress_shouldReturn400() throws Exception {
            mockMvc.perform(get(URL)
                            .param("speciality", SPECIALITY))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(bedFetchService);
        }

        @Test
        @DisplayName("GET /beds sans 'speciality' retourne 400")
        void missingSpeciality_shouldReturn400() throws Exception {
            mockMvc.perform(get(URL)
                            .param("address", ADDRESS))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(bedFetchService);
        }

        @Test
        @DisplayName("GET /beds sans aucun paramètre retourne 400")
        void missingAllParams_shouldReturn400() throws Exception {
            mockMvc.perform(get(URL))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(bedFetchService);
        }
    }

    // =========================================================================
    // 3. ERREURS DU SERVICE
    // =========================================================================
/*
    @Nested
    @DisplayName("Erreurs propagées depuis le service")
    class ServiceErrors {

        @Test
        @DisplayName("Service lance NoSuchElementException → retourne 500")
        void serviceThrowsNoSuchElement_shouldReturn500() throws Exception {
            when(bedFetchService.fetchFreeBedByNearestHospitalAndSpecialty(ADDRESS, SPECIALITY))
                    .thenThrow(new NoSuchElementException("Aucun hôpital disponible"));

            mockMvc.perform(get(URL)
                            .param("address", ADDRESS)
                            .param("speciality", SPECIALITY))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Service lance RuntimeException générique → retourne 500")
        void serviceThrowsRuntimeException_shouldReturn500() throws Exception {
            when(bedFetchService.fetchFreeBedByNearestHospitalAndSpecialty(ADDRESS, SPECIALITY))
                    .thenThrow(new RuntimeException("Erreur interne"));

            mockMvc.perform(get(URL)
                            .param("address", ADDRESS)
                            .param("speciality", SPECIALITY))
                    .andExpect(status().isInternalServerError());
        }
    }
*/
    // =========================================================================
    // Helper
    // =========================================================================

    private Bed buildBed(String name, String address, String speciality) {
        Bed bed = new Bed();
        bed.setHospitalName(name);
        bed.setHospitalAddress(address);
        bed.setSpeciality(speciality);
        return bed;
    }
}
