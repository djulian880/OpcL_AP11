package com.openclassrooms.microservice_hospital.infra;

import com.openclassrooms.microservice_hospital.domain.fetch.Hospital;
import com.openclassrooms.microservice_hospital.domain.fetch.IHospitalRepository;
import com.openclassrooms.microservice_hospital.infra.model.HospitalEntity;
import com.openclassrooms.microservice_hospital.infra.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HospitalFetcher implements IHospitalRepository {

    //@Autowired
    HospitalRepository hospitalRepository;

    @Autowired
    public HospitalFetcher(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    @Override
    public List<Hospital> getBySpeciality(String speciality) {
        List<Hospital> result=new ArrayList<>();
        List<HospitalEntity> listeHospitalBDD =hospitalRepository.findBySpecialityName(speciality);
        for(HospitalEntity hospitalBDD : listeHospitalBDD){
            Hospital hospital = new Hospital();
            hospital.setName(hospitalBDD.getName());
            hospital.setAddress(hospitalBDD.getAddress());
            result.add(hospital);
        }
        return result;
    }
}
