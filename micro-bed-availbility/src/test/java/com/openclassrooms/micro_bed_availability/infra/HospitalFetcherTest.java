package com.openclassrooms.micro_bed_availability.infra;

import com.openclassrooms.micro_bed_availability.domain.fetch.Hospital;
import com.openclassrooms.micro_bed_availability.infra.proxies.MicroGatewayProxy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HospitalFetcherTest {

    @Test
    void shouldReturnHospitals() {
        MicroGatewayProxy proxy = mock(MicroGatewayProxy.class);

        Hospital hospital = new Hospital();
        hospital.setName("Hospital A");

        when(proxy.getHospitalBySpeciality("CARDIO"))
                .thenReturn(List.of(hospital));

        HospitalFetcher fetcher = new HospitalFetcher(proxy);

        List<Hospital> result = fetcher.getHospitals("CARDIO");

        assertEquals(1, result.size());
        assertEquals("Hospital A", result.get(0).getName());
    }
}