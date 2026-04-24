package com.openclassrooms.microservice_hospital.infra.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class HospitalBDD {
    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @ElementCollection
    @CollectionTable(name = "hospitalSpecialties", joinColumns = @JoinColumn(name = "hospital_id"))
    @Column(name = "specialty")
    private ArrayList<String> specialities;



}
