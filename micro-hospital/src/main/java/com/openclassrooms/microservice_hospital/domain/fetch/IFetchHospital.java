package com.openclassrooms.microservice_hospital.domain.fetch;

import java.util.List;

public interface IFetchHospital {
    List<Hospital> findBySpecialty(String specialty);
}
