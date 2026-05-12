package com.openclassrooms.microservice_hospital.domain.fetch;

import com.openclassrooms.microservice_hospital.infra.entity.Speciality;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecialityFetchService implements IFetchSpeciality {

    private final ISpecialityRepository specialityRepository;

    public SpecialityFetchService(ISpecialityRepository specialityRepository) {
        this.specialityRepository = specialityRepository;
    }

    public List<Speciality> getAll(){
        return this.specialityRepository.getAll();
    }
}
