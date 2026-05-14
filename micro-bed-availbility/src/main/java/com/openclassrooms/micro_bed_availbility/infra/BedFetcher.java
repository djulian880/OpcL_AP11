package com.openclassrooms.micro_bed_availbility.infra;

import com.openclassrooms.micro_bed_availbility.domain.fetch.Bed;
import com.openclassrooms.micro_bed_availbility.domain.fetch.IBedRepository;
import com.openclassrooms.micro_bed_availbility.infra.model.BedsEntity;
import com.openclassrooms.micro_bed_availbility.infra.repository.BedsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BedFetcher implements IBedRepository {

    BedsRepository bedsRepository;

    @Autowired
    public BedFetcher(BedsRepository bedsRepository) {
        this.bedsRepository = bedsRepository;
    }

    public Bed getBedsByHospitalAndSpecialty(String hospital, String specialty) {
        BedsEntity bedsEntity=bedsRepository.findByHospitalNameAndSpecialityName(hospital,specialty);
        Bed result=new Bed();

        return null;
    }
}



