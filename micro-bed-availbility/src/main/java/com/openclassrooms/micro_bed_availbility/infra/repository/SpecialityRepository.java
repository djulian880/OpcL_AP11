package com.openclassrooms.micro_bed_availbility.infra.repository;

import com.openclassrooms.micro_bed_availbility.infra.model.BedsEntity;
import com.openclassrooms.micro_bed_availbility.infra.model.SpecialityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpecialityRepository extends JpaRepository<SpecialityEntity, Long> {
    Optional<SpecialityEntity> findByName(String speciality);
}
