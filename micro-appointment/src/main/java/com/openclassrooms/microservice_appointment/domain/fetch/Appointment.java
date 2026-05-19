package com.openclassrooms.microservice_appointment.domain.fetch;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class Appointment {
    private String firstName;
    private String lastName;
    private String speciality;
    private String hospital;
    private Date entranceDate;
    private Date leavingDate;

}
