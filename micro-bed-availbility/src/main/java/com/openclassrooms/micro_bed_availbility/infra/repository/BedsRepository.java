package com.openclassrooms.micro_bed_availbility.infra.repository;

import com.openclassrooms.micro_bed_availbility.infra.model.BedsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BedsRepository extends JpaRepository<BedsEntity, Long> {

    @Query(value = """
        SELECT 
            b.idBeds,
                    b.booked_beds   AS bookedBeds,
                    b.total_beds    AS totalBeds,
                    b.hospital_id   AS hospitalId,
                    b.speciality_id AS specialityId
        FROM bed b
        JOIN hospital_entity h ON h.idHospital = b.hospital_id
        JOIN speciality_entity s ON s.idSpeciality = b.speciality_id
        WHERE s.name = :specialityname
        AND h.name = :hospitalname
        """, nativeQuery = true)
    BedsEntity findByHospitalNameAndSpecialityName(@Param("hospitalname") String hospitalName, @Param("specialityname") String specialituName);
}
