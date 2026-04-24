package com.openclassrooms.microservice_hospital.domain.port;

import com.openclassrooms.microservice_hospital.domain.model.Hospital;

import java.util.List;

public interface IReturnHospital {
    List<Hospital> findBySpecialty(String specialty);
}
