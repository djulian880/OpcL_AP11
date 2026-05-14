package com.openclassrooms.micro_bed_availbility.domain.fetch;

import java.util.List;

public interface IHospitalRepository {
    List<Hospital> getHospitals(String speciality);
}
