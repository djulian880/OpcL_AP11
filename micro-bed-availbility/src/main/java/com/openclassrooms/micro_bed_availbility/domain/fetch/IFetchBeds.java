package com.openclassrooms.micro_bed_availbility.domain.fetch;

public interface IFetchBeds {
    Bed fetchFreeBedByNearestHosptialAndSpecialty(String hospital, String specialty);
}
