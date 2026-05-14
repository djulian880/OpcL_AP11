package com.openclassrooms.micro_bed_availability.infra;

import com.openclassrooms.micro_bed_availability.domain.fetch.Hospital;
import com.openclassrooms.micro_bed_availability.domain.fetch.IHospitalRepository;
import com.openclassrooms.micro_bed_availability.infra.proxies.MicroGatewayProxy;
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
