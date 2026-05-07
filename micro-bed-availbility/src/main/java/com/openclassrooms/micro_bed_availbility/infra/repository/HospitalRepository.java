package com.openclassrooms.micro_bed_availbility.infra.repository;

import com.openclassrooms.micro_bed_availbility.infra.model.BedsEntity;
import com.openclassrooms.micro_bed_availbility.infra.model.HospitalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HospitalRepository extends JpaRepository<HospitalEntity, Long> {
}
