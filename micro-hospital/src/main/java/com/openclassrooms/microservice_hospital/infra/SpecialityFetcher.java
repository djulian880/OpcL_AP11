package com.openclassrooms.microservice_hospital.infra;

import com.openclassrooms.microservice_hospital.domain.fetch.ISpecialityRepository;
import com.openclassrooms.microservice_hospital.infra.entity.Speciality;
import com.openclassrooms.microservice_hospital.infra.repository.SpecialityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SpecialityFetcher implements ISpecialityRepository {

    SpecialityRepository specialityRepository;

    @Autowired
    public SpecialityFetcher(SpecialityRepository specialityRepository) {
        this.specialityRepository = specialityRepository;
    }

    @Override
    public List<Speciality> getAll(){
        return specialityRepository.findAll();
    }

}
