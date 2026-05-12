package com.openclassrooms.microservice_hospital.controller;

import com.openclassrooms.microservice_hospital.domain.fetch.*;
import com.openclassrooms.microservice_hospital.infra.entity.Speciality;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class HospitalController {

    IFetchHospital fetchHospital;

    IFetchSpeciality fetchSpeciality;

    @Autowired
    public HospitalController(HospitalFetchService hospitalFetchService, SpecialityFetchService specialityFetchService) {
        this.fetchHospital = hospitalFetchService;
        this.fetchSpeciality = specialityFetchService;
    }

    @GetMapping(value = "/specialities")
    public List<Hospital> findHospitalBySpeciality(@RequestParam String code) {
        return fetchHospital.findBySpecialty(code);
    }

    @GetMapping(value = "/specialities/all")
    public List<Speciality> getAllSpeciality() {
        return fetchSpeciality.getAll();
    }

}
