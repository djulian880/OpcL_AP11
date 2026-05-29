package com.openclassrooms.micro_bed_availability.infra;


import com.openclassrooms.micro_bed_availability.domain.fetch.Coordinates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests UNITAIRES de CoordinatesFetcher.
 *
 * Le HttpClient interne est remplacé par un mock via ReflectionTestUtils
 * pour éviter tout appel réseau réel.
 *
 * Correctif UnfinishedStubbingException :
 * HttpClient.send() est une méthode générique : send(HttpRequest, HttpResponse.BodyHandler<T>).
 * any(HttpResponse.BodyHandler.class) ne matche pas correctement ce paramètre générique
 * et laisse le stubbing dans un état incomplet.
 * Solution : utiliser any() sans argument pour les deux paramètres.
 */
class CoordinatesFetcherTest {

    private CoordinatesFetcher coordinatesFetcher;
    private HttpClient mockHttpClient;

    @BeforeEach
    void setUp() {
        coordinatesFetcher = new CoordinatesFetcher();
        mockHttpClient = mock(HttpClient.class);
        ReflectionTestUtils.setField(coordinatesFetcher, "httpClient", mockHttpClient);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private HttpResponse<String> buildMockResponse(String body) {
        HttpResponse<String> response = (HttpResponse<String>) mock(HttpResponse.class);
        when(response.body()).thenReturn(body);
        return response;
    }

    private static final String VALID_RESPONSE = """
            {
              "features": [
                {
                  "geometry": {
                    "coordinates": [7.748, 48.573]
                  }
                }
              ]
            }
            """;

    private static final String EMPTY_FEATURES_RESPONSE = """
            {
              "features": []
            }
            """;

    // ── Tests ─────────────────────────────────────────────────────────────────

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    @DisplayName("getCoordinates() parse correctement longitude et latitude d'une réponse valide")
    void getCoordinates_validResponse_returnsCoordinates() throws Exception {
        // any() sans argument évite le problème de générique sur BodyHandler<T>
        doReturn(buildMockResponse(VALID_RESPONSE))
                .when(mockHttpClient).send(any(HttpRequest.class), any());

        Coordinates result = coordinatesFetcher.getCoordinates("1 rue de Rivoli Paris");

        assertThat(result).isNotNull();
        // L'API retourne [longitude, latitude] → coords[0]=longitude, coords[1]=latitude
        assertThat(result.getLatitude()).isEqualTo(48.573);
        assertThat(result.getLongitude()).isEqualTo(7.748);
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    @DisplayName("getCoordinates() retourne null si features est vide")
    void getCoordinates_emptyFeatures_returnsNull() throws Exception {
        doReturn(buildMockResponse(EMPTY_FEATURES_RESPONSE))
                .when(mockHttpClient).send(any(HttpRequest.class), any());

        Coordinates result = coordinatesFetcher.getCoordinates("adresse inconnue");

        assertThat(result).isNull();
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    @DisplayName("getCoordinates() retourne null si le HttpClient lève une IOException")
    void getCoordinates_httpException_returnsNull() throws Exception {
        doThrow(new IOException("Réseau indisponible"))
                .when(mockHttpClient).send(any(HttpRequest.class), any());

        Coordinates result = coordinatesFetcher.getCoordinates("1 rue Test");

        assertThat(result).isNull();
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    @DisplayName("getCoordinates() retourne null si la réponse JSON est malformée")
    void getCoordinates_malformedJson_returnsNull() throws Exception {
        doReturn(buildMockResponse("{ json invalide }"))
                .when(mockHttpClient).send(any(HttpRequest.class), any());

        Coordinates result = coordinatesFetcher.getCoordinates("1 rue Test");

        assertThat(result).isNull();
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    @DisplayName("getCoordinates() encode correctement les caractères spéciaux sans lever d'exception")
    void getCoordinates_specialCharsInAddress_doesNotThrow() throws Exception {
        doReturn(buildMockResponse(VALID_RESPONSE))
                .when(mockHttpClient).send(any(HttpRequest.class), any());

        Coordinates result = coordinatesFetcher.getCoordinates("10 rue de l'Église Strasbourg");

        assertThat(result).isNotNull();
    }
}