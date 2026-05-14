package com.openclassrooms.micro_bed_availbility.domain.fetch;

public interface IFetchBeds {
    Bed fetchFreeBedByNearestHospitalAndSpecialty(String address, String specialty);
}
