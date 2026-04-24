package com.openclassrooms.microservice_hospital.controller;

import com.openclassrooms.microservice_hospital.domain.HospitalSearchService;
import com.openclassrooms.microservice_hospital.domain.model.Hospital;
import com.openclassrooms.microservice_hospital.domain.port.IReturnHospital;
import com.openclassrooms.microservice_hospital.infra.HospitalFetcher;
import com.openclassrooms.microservice_hospital.infra.repository.HospitalRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HospitalController {

    IReturnHospital returnHospital;

    HospitalController(){
        this.returnHospital=new HospitalSearchService(new HospitalFetcher());
    }

    @GetMapping(value = "/specialty/{name}")
    public List<Hospital> findHospitalBySpeciality(@PathVariable String name) {
        return returnHospital.findBySpecialty(name);
    }
}
