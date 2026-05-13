package com.openclassrooms.microservice_appointment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration — démarre le contexte Spring complet avec une base H2 en mémoire.
 *
 * Prérequis dans src/test/resources/ :
 *   - application-test.properties  (datasource H2 + ddl-auto=create-drop)
 *   - data-test.sql                (jeu de données de test)
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/data-test.sql",    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AppointmentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // -----------------------------------------------------------------
    // Cas nominal
    // -----------------------------------------------------------------

    @Test
    @DisplayName("GET /appointments — doit retourner les rendez-vous correspondant aux critères")
    void findAppointment_shouldReturnMatchingAppointments() throws Exception {
        mockMvc.perform(get("/appointments")
                        .param("specialityCode", "CARDIO")
                        .param("hospitalName", "CHU Lyon")
                        .param("date", "2024-06-16")   // comprise entre entranceDate et leavingDate du jeu de test
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].speciality", is("CARDIO")))
                .andExpect(jsonPath("$[0].hospital", is("CHU Lyon")))
                .andExpect(jsonPath("$[0].firstName", notNullValue()))
                .andExpect(jsonPath("$[0].lastName", notNullValue()));
    }

    @Test
    @DisplayName("GET /appointments — doit retourner une liste vide si aucun rendez-vous ne correspond")
    void findAppointment_shouldReturnEmptyList_whenNoMatch() throws Exception {
        mockMvc.perform(get("/appointments")
                        .param("specialityCode", "INEXISTANT")
                        .param("hospitalName", "Hôpital Inconnu")
                        .param("date", "2000-01-01")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /appointments — une date hors de la plage ne doit retourner aucun rendez-vous")
    void findAppointment_shouldReturnEmpty_whenDateOutOfRange() throws Exception {
        // Le jeu de test a des RDV du 2024-06-15 au 2024-06-18 → 2020-01-01 ne doit rien donner
        mockMvc.perform(get("/appointments")
                        .param("specialityCode", "CARDIO")
                        .param("hospitalName", "CHU Lyon")
                        .param("date", "2020-01-01")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // -----------------------------------------------------------------
    // Validation des paramètres
    // -----------------------------------------------------------------

    @Test
    @DisplayName("GET /appointments sans paramètres — doit retourner 400")
    void findAppointment_shouldReturn400_whenNoParams() throws Exception {
        mockMvc.perform(get("/appointments")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /appointments avec un paramètre manquant — doit retourner 400")
    void findAppointment_shouldReturn400_whenOneParamMissing() throws Exception {
        mockMvc.perform(get("/appointments")
                        .param("specialityCode", "CARDIO")
                        .param("hospitalName", "CHU Lyon")
                        // date manquante
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
