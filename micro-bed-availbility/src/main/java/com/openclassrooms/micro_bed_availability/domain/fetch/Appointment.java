package com.openclassrooms.micro_bed_availability.domain.fetch;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Appointment {
    private String firstName;
    private String lastName;
    private String speciality;
    private String hospital;
    private Date entranceDate;
    private Date leavingDate;
}
