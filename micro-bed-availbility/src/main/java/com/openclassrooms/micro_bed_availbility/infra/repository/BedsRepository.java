package com.openclassrooms.micro_bed_availbility.infra.repository;

import com.openclassrooms.micro_bed_availbility.domain.fetch.Beds;
import com.openclassrooms.micro_bed_availbility.infra.model.BedsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BedsRepository extends JpaRepository<BedsEntity, Long> {

    @Query(value = """
        SELECT h.id, h.name, h.address
        FROM hospital_entity h
        JOIN hospital_speciality hs ON h.id = hs.hospital_id
        JOIN speciality_entity s ON s.id = hs.speciality_id
        WHERE s.name = :name
        """, nativeQuery = true)
    BedsEntity findByHospitalNameAndSpecialityName(@Param("hospitalname") String hospitalName, @Param("specialityname") String specialituName);
}
