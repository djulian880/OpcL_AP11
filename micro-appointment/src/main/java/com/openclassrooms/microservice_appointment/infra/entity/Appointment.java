package com.openclassrooms.microservice_appointment.infra.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@ToString
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String speciality;

    @Column(nullable = false)
    private String hospital;

    @Column(nullable = false)
    private Date entranceDate;

    @Column(nullable = false)
    private Date leavingDate;

}
