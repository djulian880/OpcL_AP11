package com.openclassrooms.microservice_hospital.infra.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Speciality {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

}
