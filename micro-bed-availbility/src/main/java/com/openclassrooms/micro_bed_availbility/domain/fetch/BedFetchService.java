package com.openclassrooms.micro_bed_availbility.domain.fetch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class BedFetchService implements IFetchBeds{


    private final IHospitalRepository hospitalRepository;
    private final IAppointmentRepository appointmentRepository;

    public BedFetchService(IHospitalRepository hospitalRepository,
                           IAppointmentRepository appointmentRepository) {

        this.hospitalRepository = hospitalRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public Bed fetchFreeBedByNearestHosptialAndSpecialty(String hospital, String speciality) {
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



        return null;
    }
}


