package com.openclassrooms.micro_bed_availbility.domain.fetch;

public interface IBedsRepository {
    Beds getBedsByHospitalAndSpecialty(String hospital, String specialty);
}
