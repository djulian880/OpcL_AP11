package com.openclassrooms.micro_bed_availbility.infra.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
@ToString
public class BedAvailabilityBDD {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String hospitalName;

    @Column(nullable = false)
    private Integer numberOfFreeBeds;
}
