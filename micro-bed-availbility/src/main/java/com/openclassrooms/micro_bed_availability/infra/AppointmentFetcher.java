package com.openclassrooms.micro_bed_availability.infra;

import com.openclassrooms.micro_bed_availability.domain.fetch.Appointment;
import com.openclassrooms.micro_bed_availability.domain.fetch.IAppointmentRepository;
import com.openclassrooms.micro_bed_availability.infra.proxies.MicroGatewayProxy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentFetcher implements IAppointmentRepository {

    private final MicroGatewayProxy microGatewayProxy;

    public AppointmentFetcher(MicroGatewayProxy microGatewayProxy){
        this.microGatewayProxy = microGatewayProxy;
    }

    @Override
    public List<Appointment> getAppointments(String hospitalName, String date, String speciality) {
        return microGatewayProxy.getAppointmentBySpecialityAndDateAndHospital(
                hospitalName,
                date,
                speciality
        );
    }
}
