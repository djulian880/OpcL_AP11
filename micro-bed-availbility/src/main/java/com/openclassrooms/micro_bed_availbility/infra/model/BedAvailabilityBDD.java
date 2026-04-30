package com.openclassrooms.micro_bed_availbility.infra.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class BedAvailabilityBDD {

    @Id
    @GeneratedValue
    Long Id;

    @Column
    String hospitalName;

    @Column
    Integer numberOfFreeBed;

}
