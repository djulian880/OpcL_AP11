package com.openclassrooms.micro_bed_availbility.infra;

import com.openclassrooms.micro_bed_availbility.domain.fetch.Beds;
import com.openclassrooms.micro_bed_availbility.domain.fetch.IBedsRepository;
import com.openclassrooms.micro_bed_availbility.infra.model.BedsEntity;
import com.openclassrooms.micro_bed_availbility.infra.repository.BedsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BedsFetcher implements IBedsRepository {

    BedsRepository bedsRepository;

    @Autowired
    public BedsFetcher(BedsRepository bedsRepository) {
        this.bedsRepository = bedsRepository;
    }

    public Beds getBedsByHospitalAndSpecialty(String hospital, String specialty) {
        BedsEntity bedsEntity=bedsRepository.findByHospitalNameAndSpecialityName(hospital,specialty);
        Beds result=new Beds();

        return null;
    }
}



