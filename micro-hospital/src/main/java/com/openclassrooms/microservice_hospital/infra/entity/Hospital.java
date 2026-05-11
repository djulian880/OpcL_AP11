package com.openclassrooms.microservice_hospital.infra.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@ToString
public class Hospital {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "hospital_speciality",                          // table de liaison
            joinColumns = @JoinColumn(name = "hospital_id"),       // FK vers hospital
            inverseJoinColumns = @JoinColumn(name = "speciality_id") // FK vers speciality
    )
    private Set<Speciality> specialities = new HashSet<>();

}
