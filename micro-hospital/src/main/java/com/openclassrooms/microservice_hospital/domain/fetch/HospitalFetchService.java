package com.openclassrooms.microservice_hospital.domain.fetch;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HospitalFetchService implements IFetchHospital {
    private final IHospitalRepository fetchHospital;

    public HospitalFetchService(IHospitalRepository fetchHospital) {
        this.fetchHospital = fetchHospital;
    }

    public List<Hospital> findBySpecialty(String specialty){
        return this.fetchHospital.getBySpeciality(specialty);
    }

}
