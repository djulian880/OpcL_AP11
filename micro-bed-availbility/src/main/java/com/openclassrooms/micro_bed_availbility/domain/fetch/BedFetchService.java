package com.openclassrooms.micro_bed_availbility.domain.fetch;

import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BedFetchService implements IFetchBeds{

    private final IBedRepository bedsRepository;
    private final IHospitalRepository hospitalRepository;
    private final IAppointmentRepository appointmentRepository;

    public BedFetchService(IBedRepository bedsRepository,
                           IHospitalRepository hospitalRepository,
                           IAppointmentRepository appointmentRepository) {
        this.bedsRepository = bedsRepository;
        this.hospitalRepository = hospitalRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public List<Bed> fetchFreeBedByNearestHosptialAndSpecialty(String hospital, String speciality) {
        // Retrieve hospitals with speciality
        List<Hospital> hospitals=hospitalRepository.getHospitals(speciality);

        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String dateString = s.format(date);

        List<Hospital> freeHospitals=new ArrayList<>();
        for(Hospital hosp:hospitals){
            List<Appointment> appointments=appointmentRepository.getAppointments(hosp.getName(),dateString,speciality);
            if(hosp.getTotalNumberOfBeds()-appointments.size()>0){
                freeHospitals.add(hosp);
            }
        }



        return this.bedsRepository.getBedsByHospitalAndSpecialty(hospital, speciality);
    }
}


