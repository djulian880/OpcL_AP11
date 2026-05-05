package com.openclassrooms.micro_bed_availbility.domain.fetch;

public interface IFetchBeds {
    Beds fetchBedsByHosptialAndSpecialty(String hospital, String specialty);
}
