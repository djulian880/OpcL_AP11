package com.openclassrooms.micro_bed_availbility.domain.fetch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.ResponsePath;
import com.graphhopper.config.CHProfile;
import com.graphhopper.config.Profile;
import com.graphhopper.util.GHUtility;
import com.graphhopper.util.PointList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class DistanceCalculatorService {

    private GraphHopper hopper;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public DistanceCalculatorService(){
        hopper = new GraphHopper();
        hopper.setOSMFile("alsace-260512.osm.pbf");
        hopper.setGraphHopperLocation("graph-cache/");   // cache du graphe
        hopper.setEncodedValuesString("car_access, car_average_speed, road_access, road_environment, max_speed, ferry_speed");

        hopper.setProfiles(new Profile("car").setCustomModel(GHUtility.loadCustomModelFromJar("car.json")));

        // this enables speed mode for the profile we called car
        hopper.getCHPreparationHandler().setCHProfiles(new CHProfile("car"));

        hopper.importOrLoad();
    }


    public double calculateDistance(String startAdress, String endAdress) {
        double[] startCoord=geocodeApiGouv(startAdress);
        double[] endCoord=geocodeApiGouv(endAdress);

        if(startCoord!=null && endCoord!=null) {
            return calculate(startCoord[0], startCoord[1], endCoord[0], endCoord[1]);
        }
        else{
            return -1.0;
        }

    }

    public double calculate(double fromLat, double fromLon, double toLat, double toLon){
        GHRequest request = new GHRequest(
                fromLat, fromLon,
                toLat, toLon
        ).setProfile("car");

        GHResponse response = hopper.route(request);

        if (!response.hasErrors()) {
            ResponsePath path = response.getBest();
            log.info("Distance : "+ path.getDistance() / 1000.0);
            log.info("Durée    : "+  path.getTime() / 60000);
            return path.getDistance();
            // Points GPS de l'itinéraire
            //PointList points = path.getPoints();
        }
        return -1.0;
    }


    private double[] geocodeApiGouv(String adresse)  {
        String encoded = URLEncoder.encode(adresse, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api-adresse.data.gouv.fr/search/?q=" + encoded + "&limit=1"))
                .GET()
                .build();

        try{
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode features = mapper.readTree(response.body()).get("features");

            if (features == null || features.isEmpty()) {
                // throw new RuntimeException("Adresse introuvable : " + adresse);
                log.error("Adresse introuvable : " + adresse);
            }

            JsonNode coords = features.get(0).get("geometry").get("coordinates");
            log.error("Coordonnées trouvées : " + coords.get(1).asDouble() +"  "+coords.get(0).asDouble() );
            return new double[]{ coords.get(1).asDouble(), coords.get(0).asDouble() };
        }
        catch (Exception e) {
            return null;
        }

    }

}
