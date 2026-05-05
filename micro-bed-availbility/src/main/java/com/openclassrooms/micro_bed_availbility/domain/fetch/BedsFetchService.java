package com.openclassrooms.micro_bed_availbility.domain.fetch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BedsFetchService implements IFetchBeds{

    private final IBedsRepository bedsRepository;

    public BedsFetchService(IBedsRepository bedsRepository) {this.bedsRepository = bedsRepository;}

    public Beds fetchBedsByHosptialAndSpecialty(String hospital, String specialty) {
        return this.bedsRepository.getBedsByHospitalAndSpecialty(hospital, specialty);
    }
}


