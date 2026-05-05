package com.openclassrooms.microservice_hospital.controller;

import com.openclassrooms.microservice_hospital.domain.fetch.HospitalFetchService;
import com.openclassrooms.microservice_hospital.domain.fetch.Hospital;
import com.openclassrooms.microservice_hospital.domain.fetch.IFetchHospital;
import com.openclassrooms.microservice_hospital.infra.HospitalSpecialityLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class HospitalController {

    IFetchHospital fetchHospital;

    @Autowired
    public HospitalController(HospitalFetchService hospitalFetchService) {
        this.fetchHospital = hospitalFetchService;
    }

    @GetMapping(value = "/specialties")
    public List<Hospital> findHospitalBySpeciality(@RequestParam String name) {
        return fetchHospital.findBySpecialty(name);
    }

}
