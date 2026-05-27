package com.openclassrooms.micro_bed_availability.infra;


import com.openclassrooms.micro_bed_availability.domain.fetch.Coordinates;
import com.openclassrooms.micro_bed_availability.domain.fetch.Hospital;
import com.openclassrooms.micro_bed_availability.infra.proxies.MicroGatewayProxy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HospitalFetcherTest {

    @Mock
    private MicroGatewayProxy microGatewayProxy;

    @InjectMocks
    private HospitalFetcher hospitalFetcher;

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Hospital buildHospital(String name) {
        Hospital h = new Hospital();
        h.setName(name);
        h.setAddress("1 rue Test");
        h.setTotalNumberOfBeds(10);
        Coordinates coords = new Coordinates();
        coords.setLatitude(48.5);
        coords.setLongitude(7.7);
        h.setCoordinates(coords);
        return h;
    }

    // ── getHospitals ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("getHospitals() délègue bien au proxy et retourne la liste")
    void getHospitals_delegatesToProxy() {
        List<Hospital> expected = List.of(buildHospital("HopA"), buildHospital("HopB"));
        when(microGatewayProxy.getHospitalBySpeciality("cardio")).thenReturn(expected);

        List<Hospital> result = hospitalFetcher.getHospitals("cardio");

        assertThat(result).isEqualTo(expected);
        verify(microGatewayProxy, times(1)).getHospitalBySpeciality("cardio");
    }

    @Test
    @DisplayName("getHospitals() retourne une liste vide si le proxy n'en renvoie aucun")
    void getHospitals_emptyList() {
        when(microGatewayProxy.getHospitalBySpeciality("ortho")).thenReturn(Collections.emptyList());

        List<Hospital> result = hospitalFetcher.getHospitals("ortho");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getHospitals() transmet exactement la spécialité reçue au proxy")
    void getHospitals_passesSpecialityAsIs() {
        when(microGatewayProxy.getHospitalBySpeciality("neurologie")).thenReturn(Collections.emptyList());

        hospitalFetcher.getHospitals("neurologie");

        verify(microGatewayProxy).getHospitalBySpeciality("neurologie");
        verify(microGatewayProxy, never()).getHospitalBySpeciality(argThat(s -> !s.equals("neurologie")));
    }
}
