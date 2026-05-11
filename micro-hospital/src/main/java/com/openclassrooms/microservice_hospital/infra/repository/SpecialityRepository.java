package com.openclassrooms.microservice_hospital.infra.repository;

import com.openclassrooms.microservice_hospital.infra.entity.Speciality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpecialityRepository extends JpaRepository<Speciality, Long> {

    Optional<Speciality> findByName(String name);
}
