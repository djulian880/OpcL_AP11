package com.openclassrooms.microservice_appointment.infra;

import com.openclassrooms.microservice_appointment.infra.entity.Appointment;
import com.openclassrooms.microservice_appointment.infra.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AppointmentFetcher implements com.openclassrooms.microservice_appointment.domain.fetch.IAppointmentRepository {

    AppointmentRepository appointmentRepository;

    @Autowired
    public AppointmentFetcher(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public List<com.openclassrooms.microservice_appointment.domain.fetch.Appointment> getBySpecialityAndHospitalAndDate(String specialityCode, String hospitalName, String date) {
        List<com.openclassrooms.microservice_appointment.domain.fetch.Appointment> result=new ArrayList<>();

        List<Appointment> listAppointments =appointmentRepository.findBySpecialityAndHospitalAndDate(specialityCode,hospitalName,date);
        for(Appointment appointmentEntity : listAppointments){
            com.openclassrooms.microservice_appointment.domain.fetch.Appointment appointment = new com.openclassrooms.microservice_appointment.domain.fetch.Appointment();
            appointment.setSpeciality(appointmentEntity.getSpeciality());
            appointment.setHospital(appointmentEntity.getHospital());
            appointment.setEntranceDate(appointmentEntity.getEntranceDate());
            appointment.setLeavingDate(appointmentEntity.getLeavingDate());
            appointment.setFirstName(appointmentEntity.getFirstName());
            appointment.setLastName(appointmentEntity.getLastName());
            result.add(appointment);
        }
        return result;
    }
}
