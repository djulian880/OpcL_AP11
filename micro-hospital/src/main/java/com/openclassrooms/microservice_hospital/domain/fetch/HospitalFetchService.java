package com.openclassrooms.microservice_hospital.domain.fetch;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HospitalFetchService implements IFetchHospital {

    private final IHospitalRepository hospitalRepository;

    public HospitalFetchService(IHospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    public List<Hospital> findBySpecialty(String specialty){
        return this.hospitalRepository.getBySpeciality(specialty);
    }

}
