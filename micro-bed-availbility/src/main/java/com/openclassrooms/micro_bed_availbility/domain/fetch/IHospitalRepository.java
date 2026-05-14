package com.openclassrooms.micro_bed_availbility.domain.fetch;

public interface IHospitalRepository {
    Hospital getHospitals(String speciality);
}
