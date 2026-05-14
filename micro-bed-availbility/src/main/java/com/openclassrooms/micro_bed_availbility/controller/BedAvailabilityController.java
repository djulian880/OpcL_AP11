package com.openclassrooms.micro_bed_availbility.controller;

import com.openclassrooms.micro_bed_availbility.domain.fetch.Bed;
import com.openclassrooms.micro_bed_availbility.domain.fetch.BedFetchService;
import com.openclassrooms.micro_bed_availbility.domain.fetch.IFetchBeds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BedAvailabilityController {

    IFetchBeds fetchBeds;

    @Autowired
    public BedAvailabilityController(BedFetchService bedFetchService) {
        this.fetchBeds = bedFetchService;
    }

    @GetMapping(value = "/beds")
    public Bed findFreeBedByNearestHospitalAndSpeciality(@RequestParam String address,
                                                         @RequestParam String speciality) {
        return fetchBeds.fetchFreeBedByNearestHospitalAndSpecialty(address, speciality);
    }

}