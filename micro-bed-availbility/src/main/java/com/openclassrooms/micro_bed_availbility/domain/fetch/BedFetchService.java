package com.openclassrooms.micro_bed_availbility.domain.fetch;

import lombok.extern.slf4j.Slf4j;
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

    public BedFetchService(IHospitalRepository hospitalRepository,
                           IAppointmentRepository appointmentRepository) {

        this.hospitalRepository = hospitalRepository;
        this.appointmentRepository = appointmentRepository;
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
            List<Appointment> appointments=appointmentRepository.getAppointments(hosp.getName(),dateString,speciality);
            int numberOfBeds=hosp.getTotalNumberOfBeds();
            int numberOfAppointments=appointments.size();
            log.info("Nombre totaux de lits: "+numberOfBeds+" / nombre de rendez-vous: "+numberOfAppointments);
            if(hosp.getTotalNumberOfBeds()-appointments.size()>0){
                log.info("Hopital avec lits libres ajouté: "+hosp.getName());
                freeHospitals.add(hosp);
            }
        }

        TreeMap<Double, Hospital> mapHospitals = new TreeMap<>();
        for(Hospital freeHosp:freeHospitals){
            double distance=distanceCalculatorService.calculateDistance(address,freeHosp.getAddress());
            log.info("Distance avec hopital: "+freeHosp.getName()+" "+distance+" m");
            mapHospitals.put(distance,freeHosp);
        }


        Bed bed=new Bed();
        bed.setHospitalAddress(mapHospitals.firstEntry().getValue().getAddress());
        bed.setHospitalName(mapHospitals.firstEntry().getValue().getName());
        bed.setSpeciality(speciality);

        return bed;
    }
}


