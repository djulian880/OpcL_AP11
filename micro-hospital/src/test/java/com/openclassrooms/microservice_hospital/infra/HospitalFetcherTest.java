package com.openclassrooms.microservice_hospital.infra;

import com.openclassrooms.microservice_hospital.infra.entity.Bed;
import com.openclassrooms.microservice_hospital.infra.entity.Coordinates;
import com.openclassrooms.microservice_hospital.infra.entity.Hospital;
import com.openclassrooms.microservice_hospital.infra.entity.Speciality;
import com.openclassrooms.microservice_hospital.infra.repository.BedRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires – HospitalFetcher")
class HospitalFetcherTest {

    @Mock
    private BedRepository bedRepository;

    @InjectMocks
    private HospitalFetcher hospitalFetcher;

    private Bed bed1;
    private Bed bed2;

    @BeforeEach
    void setUp() {
        Hospital hospital1 = new Hospital();
        hospital1.setName("CHU Strasbourg");
        hospital1.setAddress("1 Rue Molière Strasbourg");
        Coordinates coord1 = new Coordinates();
        coord1.setLatitude(37.45);
        coord1.setLongitude(42.12);
        hospital1.setCoordinates(coord1);
        Hospital hospital2 = new Hospital();
        hospital2.setName("Hôpital Civil");
        hospital2.setAddress("2 Avenue de Gaulle Strasbourg");
        Coordinates coord2 = new Coordinates();
        coord2.setLatitude(38.45);
        coord2.setLongitude(43.12);
        hospital2.setCoordinates(coord2);


        Speciality cardio = new Speciality();
        cardio.setCode("SM06");
        cardio.setName("Cardiologie");

        bed1 = new Bed();
        bed1.setHospital(hospital1);
        bed1.setSpeciality(cardio);
        bed1.setTotalNumberOfBeds(20);

        bed2 = new Bed();
        bed2.setHospital(hospital2);
        bed2.setSpeciality(cardio);
        bed2.setTotalNumberOfBeds(12);
    }

    @Test
    @DisplayName("getBySpeciality – mappe correctement Bed vers Hospital DTO")
    void getBySpeciality_shouldMapBedToHospitalDto() {
        when(bedRepository.findBySpecialityCode("SM06")).thenReturn(List.of(bed1));

        List<com.openclassrooms.microservice_hospital.domain.fetch.Hospital> result =
                hospitalFetcher.getBySpeciality("SM06");

        assertThat(result).hasSize(1);
        com.openclassrooms.microservice_hospital.domain.fetch.Hospital dto = result.get(0);
        assertThat(dto.getName()).isEqualTo("CHU Strasbourg");
        assertThat(dto.getAddress()).isEqualTo("1 Rue Molière Strasbourg");
        assertThat(dto.getTotalNumberOfBeds()).isEqualTo(20);
    }

    @Test
    @DisplayName("getBySpeciality – retourne plusieurs DTOs pour plusieurs lits")
    void getBySpeciality_shouldReturnMultipleDtos() {
        when(bedRepository.findBySpecialityCode("SM06")).thenReturn(List.of(bed1, bed2));

        List<com.openclassrooms.microservice_hospital.domain.fetch.Hospital> result =
                hospitalFetcher.getBySpeciality("SM06");

        assertThat(result).hasSize(2);
        assertThat(result).extracting(h -> h.getName())
                .containsExactlyInAnyOrder("CHU Strasbourg", "Hôpital Civil");
    }

    @Test
    @DisplayName("getBySpeciality – retourne une liste vide si aucun lit trouvé")
    void getBySpeciality_shouldReturnEmpty_whenNoBeds() {
        when(bedRepository.findBySpecialityCode("INVALID")).thenReturn(List.of());

        List<com.openclassrooms.microservice_hospital.domain.fetch.Hospital> result =
                hospitalFetcher.getBySpeciality("INVALID");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getBySpeciality – appelle le repository avec le bon code")
    void getBySpeciality_shouldCallRepositoryWithCorrectCode() {
        when(bedRepository.findBySpecialityCode(anyString())).thenReturn(List.of());

        hospitalFetcher.getBySpeciality("SM06");

        verify(bedRepository).findBySpecialityCode("SM06");
        verifyNoMoreInteractions(bedRepository);
    }
}
