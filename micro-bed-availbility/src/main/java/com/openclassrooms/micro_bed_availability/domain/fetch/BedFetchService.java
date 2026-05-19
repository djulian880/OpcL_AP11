package com.openclassrooms.micro_bed_availability.domain.fetch;

import com.openclassrooms.micro_bed_availability.infra.event.AppointmentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class BedFetchService implements IFetchBeds{


    @Autowired
    DistanceCalculatorService distanceCalculatorService;

    private final IHospitalRepository hospitalRepository;
    private final IAppointmentRepository appointmentRepository;
    private final IPublishEvent eventPublisher;
    private final ICoordinatesRepository coordinatesRepository;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public BedFetchService(IHospitalRepository hospitalRepository,
                           IAppointmentRepository appointmentRepository,
                           IPublishEvent eventPublisher,
                           ICoordinatesRepository coordinatesRepository) {

        this.hospitalRepository = hospitalRepository;
        this.appointmentRepository = appointmentRepository;
        this.eventPublisher=eventPublisher;
        this.coordinatesRepository=coordinatesRepository;

    }


    public Bed fetchFreeBedByNearestHospitalAndSpecialty(String address, String speciality) {
        String dateString = returnCurrentDate();

        CompletableFuture<List<Hospital>> fut1 = CompletableFuture.supplyAsync(() ->
                retrieveHospitals(speciality)
        );
        CompletableFuture<List<Appointment>> fut2  = CompletableFuture.supplyAsync(() ->
                retrieveAppointments(dateString, speciality)
        );
        CompletableFuture<Coordinates> fut3  = CompletableFuture.supplyAsync(() ->
                getCoordinates(address)
        );

        Hospital nearestHospital = CompletableFuture.allOf(fut1, fut2, fut3)
                .thenApply(v -> {
                    List<Hospital> hospitals = fut1.join();
                    List<Appointment> appointments = fut2.join();
                    Coordinates start = fut3.join();
                    // Traitement final ici
                    return calcNearest(hospitals, appointments, start);
                })
                .join();
        if(nearestHospital!=null){
            Bed bed=new Bed();
            bed.setHospitalAddress(nearestHospital.getAddress());
            bed.setHospitalName(nearestHospital.getName());
            bed.setSpeciality(speciality);

            new Thread(() -> {
                publishBooking( speciality, bed, dateString);        // ta méthode
            }).start();

            return bed;
        }
        return null;
    }


    public void publishBooking(String speciality,Bed bed,String dateString){
        eventPublisher.publish(new AppointmentEvent(bed.getHospitalName(),
                speciality,
                "booking",
                dateString));
    }

    public Map<String, Integer> orderAppointments(List<Hospital> hospitals,List<Appointment> appointments){
        Map<String, Integer> mapAppointments = new HashMap<>(hospitals.size());
        for(Appointment appointment : appointments){
            if(mapAppointments.containsKey(appointment.getHospital())){
                mapAppointments.replace(appointment.getHospital(),mapAppointments.get(appointment.getHospital())+1);
            }
            else{
                mapAppointments.put(appointment.getHospital(),1);
            }
        }
        return mapAppointments;
    }

    public Hospital calcNearest(List<Hospital> hospitals, List<Appointment> appointments,Coordinates start) {
        CompletableFuture<Map<String, Integer>> fut1 = CompletableFuture.supplyAsync(() ->
                orderAppointments(hospitals,appointments)
        );
        CompletableFuture<TreeMap<Double,Hospital>> fut2  = CompletableFuture.supplyAsync(() ->
                calcDistance(hospitals,start)
        );

        Hospital nearestHospital = CompletableFuture.allOf(fut1, fut2)
                .thenApply(v -> {
                    Map<String, Integer> mapAppointments = fut1.join();
                    TreeMap<Double,Hospital> distHospitals = fut2.join();
                    return findNearestAndFree(distHospitals, mapAppointments);
                })
                .join();

        return nearestHospital;
    }

    public Hospital findNearestAndFree(TreeMap<Double,Hospital> distHospitals,Map<String, Integer> mapAppointments ){
        for(Double key : distHospitals.keySet()){
            Hospital hospital = distHospitals.get(key);
            //log.info("1er dans la liste :"+hospital.getName()+" dist:"+key);
            if(mapAppointments.containsKey(hospital.getName())){
                int nbOfFreeBeds=hospital.getTotalNumberOfBeds()-mapAppointments.get(hospital.getName());
                if(nbOfFreeBeds>0){
                    return hospital;
                }
            }
        }
        return null;
    }

    public TreeMap<Double,Hospital> calcDistance(List<Hospital> hospitals,Coordinates start){
        TreeMap<Double,Hospital> map = new TreeMap<>();
        for(Hospital hospital:hospitals){
            double distance=distanceCalculatorService.calculateDistance(start,hospital.getCoordinates());
            map.put(distance,hospital);
        }
        return map;
    }

    public Coordinates getCoordinates(String address) {
        return coordinatesRepository.getCoordinates(address);
    }

    public List<Hospital> retrieveHospitals(String speciality){
        return hospitalRepository.getHospitals(speciality);
    }

    public List<Appointment> retrieveAppointments(String date,String speciality){
        return appointmentRepository.getAppointments(date,speciality);
    }

    public String returnCurrentDate(){
        Date date = new Date();
        return dateFormat.format(date);
    }

}


