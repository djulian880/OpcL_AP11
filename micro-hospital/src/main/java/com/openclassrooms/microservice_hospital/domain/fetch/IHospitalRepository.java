package com.openclassrooms.microservice_hospital.domain.fetch;

import java.util.List;

public interface IHospitalRepository {
    List<Hospital> getBySpeciality(String speciality);
}
