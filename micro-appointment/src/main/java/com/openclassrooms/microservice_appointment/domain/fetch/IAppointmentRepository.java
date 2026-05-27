package com.openclassrooms.microservice_appointment.domain.fetch;

import java.util.List;

public interface IAppointmentRepository {
    List<Appointment> getBySpecialityAndHospitalAndDate(String specialityCode, String date);
}
