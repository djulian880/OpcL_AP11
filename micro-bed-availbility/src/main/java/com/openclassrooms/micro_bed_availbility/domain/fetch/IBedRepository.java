package com.openclassrooms.micro_bed_availbility.domain.fetch;

public interface IBedRepository {
    Bed getBedsByHospitalAndSpecialty(String hospital, String specialty);
}
