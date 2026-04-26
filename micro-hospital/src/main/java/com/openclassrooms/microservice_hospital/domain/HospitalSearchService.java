package com.openclassrooms.microservice_hospital.domain;

import com.openclassrooms.microservice_hospital.domain.model.Hospital;
import com.openclassrooms.microservice_hospital.domain.port.IFetchHospital;
import com.openclassrooms.microservice_hospital.domain.port.IReturnHospital;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HospitalSearchService implements IReturnHospital {
    private final IFetchHospital fetchHospital;

    public HospitalSearchService(IFetchHospital fetchHospital) {
        this.fetchHospital = fetchHospital;
    }

    public List<Hospital> findBySpecialty(String specialty){
        return this.fetchHospital.getBySpeciality(specialty);
    }

}
