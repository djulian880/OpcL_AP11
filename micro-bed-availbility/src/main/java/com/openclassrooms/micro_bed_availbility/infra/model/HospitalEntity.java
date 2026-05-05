package com.openclassrooms.micro_bed_availbility.infra.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@ToString
public class HospitalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;
/*
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "hospital_specialities",         // nom de la table créée
            joinColumns = @JoinColumn(name = "hospital_id") // clé étrangère
    )
    @Column(name = "speciality")
    private List<String> specialities = new ArrayList<>();
*/

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "hospital_speciality",                          // table de liaison
            joinColumns = @JoinColumn(name = "hospital_id"),       // FK vers hospital
            inverseJoinColumns = @JoinColumn(name = "speciality_id") // FK vers speciality
    )
    private Set<SpecialityEntity> specialities = new HashSet<>();

}
