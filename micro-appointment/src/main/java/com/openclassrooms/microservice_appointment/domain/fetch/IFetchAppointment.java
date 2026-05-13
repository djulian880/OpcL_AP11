package com.openclassrooms.microservice_appointment.domain.fetch;

import java.util.List;

public interface IFetchAppointment {
    List<Appointment> findBySpecialityAndHospitalAndDate(String specialityCode, String hospitalName, String date);
}
