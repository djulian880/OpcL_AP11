package com.openclassrooms.micro_bed_availability.domain.fetch;

public interface IFetchBeds {
    Bed fetchFreeBedByNearestHospitalAndSpecialty(String address, String specialty);
}
