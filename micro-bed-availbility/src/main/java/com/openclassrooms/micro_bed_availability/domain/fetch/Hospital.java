package com.openclassrooms.micro_bed_availability.domain.fetch;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Hospital {
    private String name;
    private String address;
    private Integer totalNumberOfBeds;

}
