package com.openclassrooms.microservice_appointment.controller;

import com.openclassrooms.microservice_appointment.domain.fetch.Appointment;
import com.openclassrooms.microservice_appointment.domain.fetch.AppointmentFetchService;
import com.openclassrooms.microservice_appointment.domain.fetch.IFetchAppointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AppointmentController {

    IFetchAppointment fetchAppointment;

    @Autowired
    public AppointmentController(AppointmentFetchService appointmentFetchService) {
        this.fetchAppointment = appointmentFetchService;
    }

    @GetMapping(value = "/appointments")
    public List<Appointment> findAppointment(@RequestParam String specialityCode,
                                             @RequestParam String date) {
        return fetchAppointment.findBySpecialityAndHospitalAndDate(specialityCode,date);
    }


}
