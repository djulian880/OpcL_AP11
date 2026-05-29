package com.openclassrooms.micro_bed_availability.infra.event;

public record AppointmentEvent(String hospitalName,
                              String speciality,
                              String status,
                              String date) {
}
