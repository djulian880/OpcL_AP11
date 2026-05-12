package com.openclassrooms.microservice_hospital.infra;

import com.openclassrooms.microservice_hospital.domain.fetch.IHospitalRepository;
import com.openclassrooms.microservice_hospital.infra.entity.Bed;
import com.openclassrooms.microservice_hospital.infra.entity.Hospital;
import com.openclassrooms.microservice_hospital.infra.repository.BedRepository;
import com.openclassrooms.microservice_hospital.infra.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HospitalFetcher implements IHospitalRepository {

    BedRepository bedRepository;

    @Autowired
    public HospitalFetcher(BedRepository bedRepository) {
        this.bedRepository = bedRepository;
    }

    @Override
    public List<com.openclassrooms.microservice_hospital.domain.fetch.Hospital> getBySpeciality(String specialityCode) {
        List<com.openclassrooms.microservice_hospital.domain.fetch.Hospital> result=new ArrayList<>();

        List<Bed> listeBed =bedRepository.findBySpecialityCode(specialityCode);
        for(Bed bed : listeBed){
            com.openclassrooms.microservice_hospital.domain.fetch.Hospital hospital = new com.openclassrooms.microservice_hospital.domain.fetch.Hospital();
            hospital.setName(bed.getHospital().getName());
            hospital.setAddress(bed.getHospital().getAddress());
            hospital.setTotalNumberOfBeds(bed.getTotalNumberOfBeds());
            result.add(hospital);
        }
        return result;
    }
}
