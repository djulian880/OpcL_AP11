package com.openclassrooms.microservice_hospital.infra.repository;

import com.openclassrooms.microservice_hospital.infra.model.HospitalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalRepository extends JpaRepository<HospitalEntity, Long>  {

    @Query(value = """
        SELECT h.id, h.name, h.address
        FROM hospital_entity h
        JOIN hospital_speciality hs ON h.id = hs.hospital_id
        JOIN speciality_entity s ON s.id = hs.speciality_id
        WHERE s.name = :name
        """, nativeQuery = true)
    List<HospitalEntity> findBySpecialityName(@Param("name") String name);
}
