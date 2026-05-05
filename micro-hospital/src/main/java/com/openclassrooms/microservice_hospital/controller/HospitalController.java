package com.openclassrooms.microservice_hospital.controller;

import com.openclassrooms.microservice_hospital.domain.fetch.HospitalFetchService;
import com.openclassrooms.microservice_hospital.domain.fetch.Hospital;
import com.openclassrooms.microservice_hospital.domain.fetch.IFetchHospital;
import com.openclassrooms.microservice_hospital.infra.HospitalSpecialityLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HospitalController {

    IFetchHospital fetchHospital;

    @Autowired
    public HospitalController(HospitalFetchService hospitalFetchService) {
        this.fetchHospital = hospitalFetchService;
    }

    // TODO: mettre le nom dans les parame ?name=
    @GetMapping(value = "/specialties/{name}")
    public List<Hospital> findHospitalBySpeciality(@PathVariable String name) {
        return fetchHospital.findBySpecialty(name);
    }

}
