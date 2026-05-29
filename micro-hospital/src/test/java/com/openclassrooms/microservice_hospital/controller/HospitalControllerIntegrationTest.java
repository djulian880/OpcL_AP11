package com.openclassrooms.microservice_hospital.controller;

import com.openclassrooms.microservice_hospital.domain.fetch.*;
import com.openclassrooms.microservice_hospital.infra.entity.Speciality;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HospitalController.class)
@DisplayName("Tests d'intégration – HospitalController")
@WithMockUser
class HospitalControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HospitalFetchService hospitalFetchService;

    @MockitoBean
    private SpecialityFetchService specialityFetchService;

    // ────────────────────────────────────────────────────────────
    // GET /specialities
    // ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /specialities – retourne 200 et la liste des hôpitaux JSON")
    void getHospitalsBySpeciality_shouldReturn200WithList() throws Exception {
        Hospital hospital = new Hospital();
        hospital.setName("CHU Strasbourg");
        hospital.setAddress("1 Rue Molière Strasbourg");
        hospital.setTotalNumberOfBeds(25);

        when(hospitalFetchService.findBySpecialty("SM06")).thenReturn(List.of(hospital));

        mockMvc.perform(get("/specialities")
                        .param("code", "SM06")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("CHU Strasbourg")))
                .andExpect(jsonPath("$[0].address", is("1 Rue Molière Strasbourg")))
                .andExpect(jsonPath("$[0].totalNumberOfBeds", is(25)));

        verify(hospitalFetchService).findBySpecialty("SM06");
    }

    @Test
    @DisplayName("GET /specialities – retourne 200 avec liste vide si aucun hôpital")
    void getHospitalsBySpeciality_shouldReturn200WithEmptyList_whenNoneFound() throws Exception {
        when(hospitalFetchService.findBySpecialty("UNKNOWN")).thenReturn(List.of());

        mockMvc.perform(get("/specialities")
                        .param("code", "UNKNOWN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /specialities – retourne 400 si le paramètre 'code' est absent")
    void getHospitalsBySpeciality_shouldReturn400_whenCodeParamMissing() throws Exception {
        mockMvc.perform(get("/specialities")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(hospitalFetchService);
    }

    @Test
    @DisplayName("GET /specialities – retourne plusieurs hôpitaux pour une même spécialité")
    void getHospitalsBySpeciality_shouldReturnMultipleHospitals() throws Exception {
        Hospital h1 = new Hospital();
        h1.setName("CHU Strasbourg");
        h1.setAddress("1 Rue Molière Strasbourg");
        h1.setTotalNumberOfBeds(25);

        Hospital h2 = new Hospital();
        h2.setName("Hôpital Civil");
        h2.setAddress("2 Avenue de Gaulle Strasbourg");
        h2.setTotalNumberOfBeds(10);

        when(hospitalFetchService.findBySpecialty("SM06")).thenReturn(List.of(h1, h2));

        mockMvc.perform(get("/specialities")
                        .param("code", "SM06")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("CHU Strasbourg", "Hôpital Civil")));
    }

    // ────────────────────────────────────────────────────────────
    // GET /specialities/all
    // ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /specialities/all – retourne 200 avec toutes les spécialités")
    void getAllSpecialities_shouldReturn200WithList() throws Exception {
        Speciality cardio = new Speciality();
        cardio.setCode("SM06");
        cardio.setName("Cardiologie");

        Speciality onco = new Speciality();
        onco.setCode("SM10");
        onco.setName("Oncologie");

        when(specialityFetchService.getAll()).thenReturn(List.of(cardio, onco));

        mockMvc.perform(get("/specialities/all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].code", containsInAnyOrder("SM06", "SM10")))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Cardiologie", "Oncologie")));

        verify(specialityFetchService).getAll();
    }

    @Test
    @DisplayName("GET /specialities/all – retourne 200 avec liste vide si base vide")
    void getAllSpecialities_shouldReturn200WithEmptyList_whenNone() throws Exception {
        when(specialityFetchService.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/specialities/all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /specialities/all – le champ 'id' est masqué (@JsonIgnore)")
    void getAllSpecialities_shouldNotExposeId() throws Exception {
        Speciality cardio = new Speciality();
        cardio.setCode("SM06");
        cardio.setName("Cardiologie");

        when(specialityFetchService.getAll()).thenReturn(List.of(cardio));

        mockMvc.perform(get("/specialities/all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").doesNotExist());
    }
}
