package com.openclassrooms.microservice_hospital.controller;

import com.openclassrooms.microservice_hospital.domain.HospitalSearchService;
import com.openclassrooms.microservice_hospital.domain.model.Hospital;
import com.openclassrooms.microservice_hospital.domain.port.IReturnHospital;
import com.openclassrooms.microservice_hospital.infra.HospitalFHIRClient;
import com.openclassrooms.microservice_hospital.infra.HospitalFetcher;
import com.openclassrooms.microservice_hospital.infra.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HospitalController {

    IReturnHospital returnHospital;

    @Autowired
    HospitalFHIRClient hospitalFHIRClient;

    @Autowired
    public HospitalController(HospitalSearchService hospitalSearchService) {
        this.returnHospital = hospitalSearchService;
    }


    @GetMapping(value = "/specialty/{name}")
    public List<Hospital> findHospitalBySpeciality(@PathVariable String name) {
        return returnHospital.findBySpecialty(name);
    }

    // Pour développement
    @RequestMapping(value = "/hospital/retrieveall")
    public String retrieveAllHospitalFromFHIR() {
        hospitalFHIRClient.retrieveAll();
        return "Done";
    }
}
