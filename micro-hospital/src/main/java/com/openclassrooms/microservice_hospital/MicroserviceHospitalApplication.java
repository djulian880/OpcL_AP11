package com.openclassrooms.microservice_hospital;

import com.openclassrooms.microservice_hospital.infra.HospitalFHIRClient;
import com.openclassrooms.microservice_hospital.infra.model.HospitalBDD;
import com.openclassrooms.microservice_hospital.infra.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

@SpringBootApplication
public class MicroserviceHospitalApplication {



	public static void main(String[] args) {

		SpringApplication.run(MicroserviceHospitalApplication.class, args);

	}


}
