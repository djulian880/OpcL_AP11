package com.openclassrooms.micro_bed_availbility.domain.fetch;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Beds {
    String hospitalName;
    String specialityName;
    int totalFreeBeds;
}
