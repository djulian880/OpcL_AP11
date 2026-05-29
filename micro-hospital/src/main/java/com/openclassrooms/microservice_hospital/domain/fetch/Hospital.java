package com.openclassrooms.microservice_hospital.domain.fetch;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Hospital {
    private String name;
    private String address;
    private Integer totalNumberOfBeds;
    private Coordinates coordinates;
}
