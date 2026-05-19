package com.openclassrooms.micro_bed_availability.domain.fetch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.ResponsePath;
import com.graphhopper.config.CHProfile;
import com.graphhopper.config.Profile;
import com.graphhopper.util.GHUtility;
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


    public double calculateDistance(Coordinates start, Coordinates end){
        GHRequest request = new GHRequest(
                start.getLatitude(), start.getLongitude(),
                end.getLatitude(), end.getLongitude()
        ).setProfile("car");

        GHResponse response = hopper.route(request);

        if (!response.hasErrors()) {
            ResponsePath path = response.getBest();
            //log.info("Distance : "+ path.getDistance() / 1000.0);
            //log.info("Durée    : "+  path.getTime() / 60000);
            return path.getDistance();
            // Points GPS de l'itinéraire
            //PointList points = path.getPoints();
        }
        else{
            log.error(response.getErrors().toString());
        }
        return -1.0;
    }

}
