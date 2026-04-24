package com.openclassrooms.microservice_hospital.infra;

import com.openclassrooms.microservice_hospital.domain.model.Hospital;
import com.openclassrooms.microservice_hospital.domain.port.IFetchHospital;
import com.openclassrooms.microservice_hospital.infra.model.HospitalBDD;
import com.openclassrooms.microservice_hospital.infra.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HospitalFetcher implements IFetchHospital {
    @Autowired
    HospitalRepository hospitalRepository;

    @Override
    public List<Hospital> getBySpeciality(String speciality) {
        List<Hospital> result=new ArrayList<>();
        List<HospitalBDD> listeHospitalBDD =hospitalRepository.findBySpecialitiesContaining(speciality);
        for(HospitalBDD hospitalBDD : listeHospitalBDD){
            Hospital hospital = new Hospital();
            hospital.setName(hospitalBDD.getName());
            hospital.setAddress(hospitalBDD.getAddress());
            result.add(hospital);
        }

        return result;
    }
}
