package com.openclassrooms.micro_bed_availbility.domain.fetch;

import java.util.List;

public interface IAppointmentRepository {
    List<Appointment> getAppointments(String hospitalName, String date, String speciality);
}
