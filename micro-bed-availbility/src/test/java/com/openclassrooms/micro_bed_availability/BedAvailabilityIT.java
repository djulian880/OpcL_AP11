package com.openclassrooms.micro_bed_availability;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.openclassrooms.micro_bed_availability.domain.fetch.Appointment;
import com.openclassrooms.micro_bed_availability.domain.fetch.Coordinates;
import com.openclassrooms.micro_bed_availability.domain.fetch.Hospital;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestClient;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.anyString;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithMockUser()
class BedAvailabilityIT {

    @TestConfiguration
    static class TestConfig {

        @Bean
        public feign.codec.Decoder feignDecoder() {
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            return new feign.jackson.JacksonDecoder(mapper);
        }
    }

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    private static final WireMockServer wireMockServer = new WireMockServer(0);

    private RestClient restClient;

    private final ObjectMapper mapper = new ObjectMapper();

    private static final String URL        = "/beds";
    private static final String SPECIALITY = "cardiologie";
    // Adresse de l'appelant : Strasbourg, coordonnées = (48.5734, 7.7521) dans data.sql
    private static final String ADDRESS    = "1 rue Principale, Strasbourg";

    /** Stub WireMock pour GET /Hospital/specialities. */
    private void stubGatewayHospitals(List<Hospital> payload) throws Exception {
        wireMockServer.stubFor(get(urlPathEqualTo("/Hospital/specialities"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json") // Version string standard
                        .withBody(payload.isEmpty() ? "[]" : mapper.writeValueAsString(payload))));
    }

    /** Stub WireMock pour GET /Appointment/appointments. */
    private void stubGatewayAppointments(List<Appointment> appointments) throws Exception {
        wireMockServer.stubFor(get(urlPathEqualTo("/Appointment/appointments"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(appointments.isEmpty() ? "[]" : mapper.writeValueAsString(appointments))));
    }


    @BeforeAll
    static void startWireMock() {
        wireMockServer.start();
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }

    @BeforeEach
    void resetWireMock() {
        wireMockServer.resetAll();
    }

    @BeforeEach
    void setUp(@Autowired RestClient.Builder builder, @LocalServerPort int port) {
        // Crée le client dynamiquement avec le port aléatoire injecté
        this.restClient = builder
                .baseUrl("http://localhost:" + port)
                .build();
    }


    @DynamicPropertySource
    static void feignUrl(DynamicPropertyRegistry registry) {
        registry.add("feign.client.url.microservice-gateway",
                () -> "http://localhost:" + wireMockServer.port());
        registry.add("gateway.api-url", wireMockServer::baseUrl);
        registry.add("external-service.api-url", wireMockServer::baseUrl);

    }


    @Test
    void shouldFetchDataThroughGatewayAndExternalService() throws Exception {

        stubGatewayAppointments(new ArrayList<>());

        Hospital mockHospital = new Hospital();
        mockHospital.setName("Hôpital Strasbourg Cardio");
        mockHospital.setAddress("1 rue Principale, Strasbourg");
        mockHospital.setTotalNumberOfBeds(10);
        Coordinates coord=new Coordinates();
        coord.setLatitude(48.5734);
        coord.setLongitude(7.7521);
        mockHospital.setCoordinates(coord);


        List<Hospital> hospitals = List.of(mockHospital);
        stubGatewayHospitals(hospitals);


        var response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URL) // "/beds"
                        .queryParam("address", ADDRESS) // "1 rue Principale, Strasbourg"
                        .queryParam("speciality", SPECIALITY) // "cardiologie"
                        .build())
                .retrieve()
                .toEntity(String.class);

        ArgumentCaptor<Object> messageCaptor = ArgumentCaptor.forClass(Object.class);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            org.mockito.Mockito.verify(rabbitTemplate).convertAndSend(anyString(), anyString(), messageCaptor.capture());

            // Inspecter l'événement capturé
            Object eventSent = messageCaptor.getValue();
            assertThat(eventSent.toString()).contains("booking");
        });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Strasbourg");

        wireMockServer.verify(1, getRequestedFor(urlPathMatching("/Hospital/specialities")));
        wireMockServer.verify(1, getRequestedFor(urlPathMatching("/Appointment/appointments")));
    }

}