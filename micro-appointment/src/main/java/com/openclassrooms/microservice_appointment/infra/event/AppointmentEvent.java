package com.openclassrooms.microservice_appointment.infra.event;

public record AppointmentEvent(String hospitalName,
                               String speciality,
                               String status,
                               String date) {
}
