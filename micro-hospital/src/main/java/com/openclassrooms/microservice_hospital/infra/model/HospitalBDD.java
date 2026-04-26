package com.openclassrooms.microservice_hospital.infra.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString
public class HospitalBDD {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "hospital_specialities",         // nom de la table créée
            joinColumns = @JoinColumn(name = "hospital_id") // clé étrangère
    )
    @Column(name = "speciality")
    private List<String> specialities = new ArrayList<>();



}
