package com.openclassrooms.microservice_appointment.controller;

import com.openclassrooms.microservice_appointment.domain.fetch.Appointment;
import com.openclassrooms.microservice_appointment.domain.fetch.AppointmentFetchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
@DisplayName("Tests d'intégration – HospitalController")
@WithMockUser
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppointmentFetchService appointmentFetchService;

    private Appointment appointment1;
    private Appointment appointment2;

    @BeforeEach
    void setUp() {
        appointment1 = new Appointment();
        appointment1.setFirstName("Jean");
        appointment1.setLastName("Dupont");
        appointment1.setSpeciality("CARDIO");
        appointment1.setHospital("CHU Lyon");
        appointment1.setEntranceDate(new Date(1718400000000L));
        appointment1.setLeavingDate(new Date(1718659200000L));

        appointment2 = new Appointment();
        appointment2.setFirstName("Marie");
        appointment2.setLastName("Martin");
        appointment2.setSpeciality("CARDIO");
        appointment2.setHospital("CHU Lyon");
        appointment2.setEntranceDate(new Date(1718400000000L));
        appointment2.setLeavingDate(new Date(1718745600000L));
    }

    @Test
    @DisplayName("GET /appointments doit retourner 200 avec la liste des rendez-vous")
    void findAppointment_shouldReturn200WithAppointments() throws Exception {
        // Arrange
        when(appointmentFetchService.findBySpecialityAndHospitalAndDate("CARDIO", "2024-06-15"))
                .thenReturn(List.of(appointment1, appointment2));

        // Act & Assert
        mockMvc.perform(get("/appointments")
                        .param("specialityCode", "CARDIO")
                        .param("hospitalName", "CHU Lyon")
                        .param("date", "2024-06-15")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", is("Jean")))
                .andExpect(jsonPath("$[0].lastName", is("Dupont")))
                .andExpect(jsonPath("$[0].speciality", is("CARDIO")))
                .andExpect(jsonPath("$[0].hospital", is("CHU Lyon")))
                .andExpect(jsonPath("$[1].firstName", is("Marie")))
                .andExpect(jsonPath("$[1].lastName", is("Martin")));
    }

    @Test
    @DisplayName("GET /appointments doit retourner une liste vide si aucun résultat")
    void findAppointment_shouldReturn200WithEmptyList_whenNoResults() throws Exception {
        // Arrange
        when(appointmentFetchService.findBySpecialityAndHospitalAndDate( any(), any()))
                .thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/appointments")
                        .param("specialityCode", "NEURO")
                        .param("hospitalName", "CHU Paris")
                        .param("date", "2024-01-01")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /appointments sans paramètres doit retourner 400")
    void findAppointment_shouldReturn400_whenMissingParams() throws Exception {
        mockMvc.perform(get("/appointments")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /appointments doit transmettre les bons paramètres au service")
    void findAppointment_shouldForwardCorrectParamsToService() throws Exception {
        // Arrange
        when(appointmentFetchService.findBySpecialityAndHospitalAndDate("PNEUMO", "2024-09-10"))
                .thenReturn(List.of());

        // Act
        mockMvc.perform(get("/appointments")
                        .param("specialityCode", "PNEUMO")
                        .param("hospitalName", "Hôpital Saint-Louis")
                        .param("date", "2024-09-10"))
                .andExpect(status().isOk());

        // Assert
        verify(appointmentFetchService, times(1))
                .findBySpecialityAndHospitalAndDate("PNEUMO", "2024-09-10");
    }
}
