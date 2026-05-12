package com.openclassrooms.microservice_hospital.infra.repository;

import com.openclassrooms.microservice_hospital.infra.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long>  {

}
