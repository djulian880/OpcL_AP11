package com.openclassrooms.microservice_appointment.domain.fetch;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentFetchService implements IFetchAppointment {

    private final IAppointmentRepository appointmentRepository;

    public AppointmentFetchService(IAppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public List<Appointment> findBySpecialityAndHospitalAndDate(String specialityCode, String date) {
        return this.appointmentRepository.getBySpecialityAndHospitalAndDate(specialityCode,date);
    }
}
