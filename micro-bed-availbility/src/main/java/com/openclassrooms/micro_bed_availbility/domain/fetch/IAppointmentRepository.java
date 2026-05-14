package com.openclassrooms.micro_bed_availbility.domain.fetch;

public interface IAppointmentRepository {
    Appointment getAppointments(String hospitalName, String date, String speciality);
}
