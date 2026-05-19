package com.openclassrooms.micro_bed_availability.infra;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.micro_bed_availability.domain.fetch.Coordinates;
import com.openclassrooms.micro_bed_availability.domain.fetch.ICoordinatesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class CoordinatesFetcher implements ICoordinatesRepository {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Coordinates getCoordinates(String address) {
        Coordinates result = null;

        String encoded = URLEncoder.encode(address, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api-adresse.data.gouv.fr/search/?q=" + encoded + "&limit=1"))
                .GET()
                .build();

        try{
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode features = mapper.readTree(response.body()).get("features");

            if (features == null || features.isEmpty()) {

            }

            JsonNode coords = features.get(0).get("geometry").get("coordinates");
            //log.info("Coordonnées trouvées : " + coords.get(1).asDouble() +"  "+coords.get(0).asDouble() );
            result.setLatitude(coords.get(1).asDouble());
            result.setLongitude(coords.get(0).asDouble());

        }
        catch (Exception e) {
            return result;
        }

        return result;
    }

}
