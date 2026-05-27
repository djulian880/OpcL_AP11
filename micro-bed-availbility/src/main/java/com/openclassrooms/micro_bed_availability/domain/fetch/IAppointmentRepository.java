package com.openclassrooms.micro_bed_availability.domain.fetch;

import java.util.List;

public interface IAppointmentRepository {
    List<Appointment> getAppointments(String date, String speciality);
}
