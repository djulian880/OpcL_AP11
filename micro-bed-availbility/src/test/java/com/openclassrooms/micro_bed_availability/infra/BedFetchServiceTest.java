package com.openclassrooms.micro_bed_availability.infra;

import com.openclassrooms.micro_bed_availability.domain.fetch.*;
import com.openclassrooms.micro_bed_availability.infra.event.AppointmentEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BedFetchServiceTest {

    @Mock
    private IHospitalRepository hospitalRepository;

    @Mock
    private IAppointmentRepository appointmentRepository;

    @Mock
    private IPublishEvent publishEvent;

    @Mock
    private ICoordinatesRepository coordinatesRepository;

    @Mock
    private DistanceCalculatorService distanceCalculatorService;

    @InjectMocks
    private BedFetchService service;

    private Hospital hospital1;
    private Hospital hospital2;

    @BeforeEach
    void setUp() {
        hospital1 = new Hospital();
        hospital1.setName("Hospital A");
        hospital1.setTotalNumberOfBeds(5);
        hospital1.setCoordinates(new Coordinates());

        hospital2 = new Hospital();
        hospital2.setName("Hospital B");
        hospital2.setTotalNumberOfBeds(2);
        hospital2.setCoordinates(new Coordinates());
    }

    @Test
    void shouldOrderAppointments() {
        Appointment appointment1 = new Appointment();
        appointment1.setHospital("Hospital A");

        Appointment appointment2 = new Appointment();
        appointment2.setHospital("Hospital A");

        Map<String, Integer> result = service.orderAppointments(
                List.of(hospital1),
                List.of(appointment1, appointment2)
        );

        assertEquals(2, result.get("Hospital A"));
    }

    @Test
    void shouldFindNearestHospitalWithFreeBed() {
        TreeMap<Double, Hospital> map = new TreeMap<>();
        map.put(10.0, hospital1);

        Map<String, Integer> appointments = Map.of(
                "Hospital A", 2
        );

        Hospital result = service.findNearestAndFree(map, appointments);

        assertNotNull(result);
        assertEquals("Hospital A", result.getName());
    }

    @Test
    void shouldReturnNullWhenNoBedsAvailable() {
        TreeMap<Double, Hospital> map = new TreeMap<>();
        map.put(10.0, hospital2);

        Map<String, Integer> appointments = Map.of(
                "Hospital B", 2
        );

        Hospital result = service.findNearestAndFree(map, appointments);

        assertNull(result);
    }

    @Test
    void shouldCalculateDistances() {
        Coordinates start = new Coordinates();

        when(distanceCalculatorService.calculateDistance(any(), any()))
                .thenReturn(10.0)
                .thenReturn(20.0);

        TreeMap<Double, Hospital> result = service.calcDistance(
                List.of(hospital1, hospital2),
                start
        );

        assertEquals(2, result.size());
    }

    @Test
    void shouldPublishBooking() {
        Bed bed = new Bed();
        bed.setHospitalName("Hospital A");

        service.publishBooking("CARDIO", bed, "2026-01-01");

        verify(publishEvent).publish(any(AppointmentEvent.class));
    }
}