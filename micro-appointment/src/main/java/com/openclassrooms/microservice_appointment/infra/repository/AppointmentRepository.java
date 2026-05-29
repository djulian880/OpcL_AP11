package com.openclassrooms.microservice_appointment.infra.repository;

import com.openclassrooms.microservice_appointment.infra.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<com.openclassrooms.microservice_appointment.infra.entity.Appointment, Long>  {

    @Query(value = """
        SELECT *
        FROM appointment a
        WHERE a.speciality= :specialityCode
        AND a.entrance_date<= :date
        AND a.leaving_date>= :date
        """, nativeQuery = true)
    List<Appointment> findBySpecialityAndHospitalAndDate(@Param("specialityCode") String specialityCode,
                                                   @Param("date") String date);
}
