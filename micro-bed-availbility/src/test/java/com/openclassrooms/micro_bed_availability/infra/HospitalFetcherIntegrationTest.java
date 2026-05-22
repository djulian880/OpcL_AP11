package com.openclassrooms.micro_bed_availability.infra;

import com.openclassrooms.micro_bed_availability.domain.fetch.DistanceCalculatorService;
import com.openclassrooms.micro_bed_availability.domain.fetch.Hospital;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests d'intégration pour HospitalFetcher.
 *
 * Utilise MockWebServer (OkHttp) : pas de dépendance Jetty, aucun conflit
 * avec Spring Boot 3.
 *
 * Dépendance à ajouter dans pom.xml si absente :
 *   <dependency>
 *       <groupId>com.squareup.okhttp3</groupId>
 *       <artifactId>mockwebserver</artifactId>
 *       <scope>test</scope>
 *   </dependency>
 *
 * Les exclusions d'autoconfiguration sont déclarées ICI (sur l'annotation)
 * ET dans application-test.yml pour une couverture maximale selon la version
 * de Spring Boot utilisée.
 */
@SpringBootTest()

@ActiveProfiles("test")
@Tag("integration")
class HospitalFetcherIntegrationTest {

    private static MockWebServer mockWebServer;

    @Autowired
    private HospitalFetcher hospitalFetcher;

    // Empêche le constructeur GraphHopper/OSM de crasher au démarrage
    @MockitoBean
    private DistanceCalculatorService distanceCalculatorService;

    // ── Cycle de vie ──────────────────────────────────────────────────────────

    /**
     * @DynamicPropertySource est évalué par Spring AVANT le démarrage du contexte,
     * ce qui garantit que l'URL MockWebServer est disponible quand Feign se configure.
     */
    @DynamicPropertySource
    static void registerMockServerUrl(DynamicPropertyRegistry registry) throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        registry.add("feign.client.url.microservice-gateway",
                () -> "http://localhost:" + mockWebServer.getPort());
    }

    @AfterAll
    static void stopMockServer() throws IOException {
        if (mockWebServer != null) {
            mockWebServer.shutdown();
        }
    }

    // ── Scénarios ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("[IT] HospitalFetcher désérialise correctement la réponse JSON du gateway")
    void getHospitals_parsesGatewayResponse() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("""
                        [
                          {
                            "name": "Hôpital Alpha",
                            "address": "1 avenue Alpha",
                            "totalNumberOfBeds": 10,
                            "coordinates": { "latitude": 48.5, "longitude": 7.7 }
                          },
                          {
                            "name": "Hôpital Bêta",
                            "address": "2 rue Bêta",
                            "totalNumberOfBeds": 5,
                            "coordinates": { "latitude": 48.6, "longitude": 7.8 }
                          }
                        ]
                        """));

        List<Hospital> result = hospitalFetcher.getHospitals("cardio");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Hôpital Alpha");
        assertThat(result.get(0).getTotalNumberOfBeds()).isEqualTo(10);
        assertThat(result.get(1).getName()).isEqualTo("Hôpital Bêta");
        assertThat(result.get(1).getCoordinates().getLatitude()).isEqualTo(48.6);
        assertThat(result.get(1).getCoordinates().getLongitude()).isEqualTo(7.8);

        RecordedRequest request = mockWebServer.takeRequest(2, TimeUnit.SECONDS);
        assertThat(request).isNotNull();
        assertThat(request.getPath()).contains("/Hospital/specialities");
        assertThat(request.getPath()).contains("code=cardio");
    }

    @Test
    @DisplayName("[IT] HospitalFetcher retourne une liste vide si le gateway renvoie []")
    void getHospitals_emptyArray() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("[]"));

        List<Hospital> result = hospitalFetcher.getHospitals("dermatologie");

        RecordedRequest request = mockWebServer.takeRequest(2, TimeUnit.SECONDS);
        assertThat(result).isEmpty();


        assertThat(request).isNotNull();
        assertThat(request.getPath()).contains("code=dermatologie");
    }

    @Test
    @DisplayName("[IT] HospitalFetcher lève une exception Feign si le gateway répond 500")
    void getHospitals_serverError_throwsException() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        Assertions.assertThrows(Exception.class,
                () -> hospitalFetcher.getHospitals("ortho"));
        RecordedRequest request = mockWebServer.takeRequest(2, TimeUnit.SECONDS);
        assertThat(request).isNotNull();
        assertThat(request.getPath()).contains("code=ortho");
    }

    @Test
    @DisplayName("[IT] HospitalFetcher envoie bien le paramètre 'code' dans la query string")
    void getHospitals_sendsCorrectQueryParam() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("[]"));

        hospitalFetcher.getHospitals("neurologie");

        RecordedRequest request = mockWebServer.takeRequest(2, TimeUnit.SECONDS);
        assertThat(request).isNotNull();
        assertThat(request.getPath()).contains("code=neurologie");
    }
}