package com.openclassrooms.microservice_hospital.infra.repository;

import com.openclassrooms.microservice_hospital.domain.model.Hospital;
import com.openclassrooms.microservice_hospital.infra.model.HospitalBDD;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalRepository extends JpaRepository<HospitalBDD, Long>  {

    List<HospitalBDD> findBySpecialitiesContaining(String specialty);
}
