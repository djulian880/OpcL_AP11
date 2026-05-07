package com.openclassrooms.micro_bed_availbility.infra.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class SpecialityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSpeciality;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

}
