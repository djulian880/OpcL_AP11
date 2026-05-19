package com.openclassrooms.micro_bed_availability.domain.fetch;

import ca.uhn.fhir.rest.gclient.IUntypedQuery;
import com.openclassrooms.micro_bed_availability.infra.event.AppointmentEvent;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class BedFetchService implements IFetchBeds{


    @Autowired
    DistanceCalculatorService distanceCalculatorService;

    private final IHospitalRepository hospitalRepository;
    private final IAppointmentRepository appointmentRepository;
    private final IPublishEvent eventPublisher;
    private final ICoordinatesRepository coordinatesRepository;

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
        // Retrieve hospitals with speciality
        List<Hospital> hospitals=hospitalRepository.getHospitals(speciality);

        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String dateString = s.format(date);

        List<Hospital> freeHospitals=new ArrayList<>();
        for(Hospital hosp:hospitals){
            log.info("Recherche des rdv pour l'hopital: "+hosp.getName()+" / avec la spécialité: "+speciality);
            List<Appointment> appointments=appointmentRepository.getAppointments(dateString,speciality);
            int numberOfBeds=hosp.getTotalNumberOfBeds();
            int numberOfAppointments=appointments.size();
            log.info("Nombre totaux de lits: "+numberOfBeds+" / nombre de rendez-vous: "+numberOfAppointments);
            if(hosp.getTotalNumberOfBeds()-appointments.size()>0){
                log.info("Hopital avec lits libres ajouté: "+hosp.getName());
                freeHospitals.add(hosp);
            }
        }

        //Find coordinates of start address
        Coordinates start=coordinatesRepository.getCoordinates(address);

        TreeMap<Double, Hospital> mapHospitals = new TreeMap<>();
        for(Hospital freeHosp:freeHospitals){
            double distance=distanceCalculatorService.calculateDistance(start,freeHosp.getCoordinates());
            log.info("Distance avec hopital: "+freeHosp.getName()+" "+distance+" m");
            mapHospitals.put(distance,freeHosp);
        }


        Bed bed=new Bed();
        bed.setHospitalAddress(mapHospitals.firstEntry().getValue().getAddress());
        bed.setHospitalName(mapHospitals.firstEntry().getValue().getName());
        bed.setSpeciality(speciality);

        eventPublisher.publish(new AppointmentEvent(bed.getHospitalName(),
                speciality,
                "booking",
                dateString));

        return bed;
    }
}


