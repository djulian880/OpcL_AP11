package com.openclassrooms.micro_bed_availbility.infra.proxies;

import com.openclassrooms.micro_bed_availbility.configuration.FeignClientConfig;
import com.openclassrooms.micro_bed_availbility.domain.fetch.Appointment;
import com.openclassrooms.micro_bed_availbility.domain.fetch.Hospital;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "microservice-gateway", url = "${feign.client.url.microservice-gateway}",  configuration = FeignClientConfig.class)
public interface MicroGatewayProxy {
    @GetMapping(value = "/Appointment/appointments")
    public List<Appointment> getAppointmentBySpecialityAndDateAndHospital(
            @RequestParam("hospitalName") String hospitalName,
            @RequestParam("date") String date,
            @RequestParam("specialityCode") String speciality
    );

    @GetMapping(value = "/Hospital/specialities")
    public List<Hospital> getHospitalBySpeciality(
            @RequestParam("code") String specialityCode
    );


}


