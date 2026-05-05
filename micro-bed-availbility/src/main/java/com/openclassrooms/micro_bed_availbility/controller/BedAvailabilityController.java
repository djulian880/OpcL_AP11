package com.openclassrooms.micro_bed_availbility.controller;

import com.openclassrooms.micro_bed_availbility.domain.fetch.Beds;
import com.openclassrooms.micro_bed_availbility.domain.fetch.BedsFetchService;
import com.openclassrooms.micro_bed_availbility.domain.fetch.IFetchBeds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BedAvailabilityController {

    IFetchBeds fetchBeds;

    @Autowired
    public BedAvailabilityController(BedsFetchService bedsFetchService) {
        this.fetchBeds = bedsFetchService;
    }

    @GetMapping(value = "/beds")
    public Beds findBedsByHospitalAndSpeciality(@RequestParam String hospitalName, @RequestParam String specialityName) {
        return fetchBeds.fetchBedsByHosptialAndSpecialty(hospitalName, specialityName);
    }

}