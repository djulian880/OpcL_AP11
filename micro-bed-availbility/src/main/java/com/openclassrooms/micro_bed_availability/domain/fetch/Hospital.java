package com.openclassrooms.micro_bed_availability.domain.fetch;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Hospital {
    private String name;
    private String address;
    private Integer totalNumberOfBeds;
    private Coordinates coordinates;

}
