package com.openclassrooms.microservice_hospital.domain.fetch;

import com.openclassrooms.microservice_hospital.infra.entity.Speciality;

import java.util.List;

public interface IFetchSpeciality {
    List<Speciality> getAll();
}
