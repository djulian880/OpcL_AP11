package com.openclassrooms.micro_bed_availbility.infra;

import com.openclassrooms.micro_bed_availbility.domain.fetch.Hospital;
import com.openclassrooms.micro_bed_availbility.domain.fetch.IHospitalRepository;
import com.openclassrooms.micro_bed_availbility.infra.proxies.MicroGatewayProxy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HospitalFetcher  implements IHospitalRepository {

    private final MicroGatewayProxy microGatewayProxy;

    public HospitalFetcher(MicroGatewayProxy microGatewayProxy){
        this.microGatewayProxy = microGatewayProxy;
    }

    @Override
    public List<Hospital> getHospitals(String speciality) {
        return microGatewayProxy.getHospitalBySpeciality(speciality);
    }
}
