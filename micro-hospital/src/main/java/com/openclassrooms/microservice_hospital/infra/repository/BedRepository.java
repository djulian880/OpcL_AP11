package com.openclassrooms.microservice_hospital.infra.repository;

import com.openclassrooms.microservice_hospital.infra.entity.Bed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BedRepository extends JpaRepository<Bed, Long> {

    @Query(value = """
        SELECT b.id, b.total_number_of_beds, b.hospital_id, b.speciality_id
        FROM bed b
        JOIN hospital h ON h.id=b.hospital_id
        JOIN speciality s ON b.speciality_id = s.id
        WHERE s.code = :code
        """, nativeQuery = true)
    List<Bed> findBySpecialityCode(@Param("code") String code);
}
