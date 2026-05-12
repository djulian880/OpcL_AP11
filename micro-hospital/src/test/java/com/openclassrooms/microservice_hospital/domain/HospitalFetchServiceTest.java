package com.openclassrooms.microservice_hospital.domain;

import com.openclassrooms.microservice_hospital.domain.fetch.Hospital;
import com.openclassrooms.microservice_hospital.domain.fetch.HospitalFetchService;
import com.openclassrooms.microservice_hospital.domain.fetch.IHospitalRepository;
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
@DisplayName("Tests unitaires – HospitalFetchService")
class HospitalFetchServiceTest {

    @Mock
    private IHospitalRepository hospitalRepository;

    @InjectMocks
    private HospitalFetchService hospitalFetchService;

    private Hospital cardiology;
    private Hospital oncology;

    @BeforeEach
    void setUp() {
        cardiology = new Hospital();
        cardiology.setName("CHU Strasbourg");
        cardiology.setAddress("1 Rue Molière Strasbourg");
        cardiology.setTotalNumberOfBeds(25);

        oncology = new Hospital();
        oncology.setName("Hôpital Civil");
        oncology.setAddress("2 Avenue de Gaulle Strasbourg");
        oncology.setTotalNumberOfBeds(15);
    }

    @Test
    @DisplayName("findBySpecialty – retourne la liste des hôpitaux pour un code valide")
    void findBySpecialty_shouldReturnHospitals_whenCodeExists() {
        when(hospitalRepository.getBySpeciality("SM06")).thenReturn(List.of(cardiology));

        List<Hospital> result = hospitalFetchService.findBySpecialty("SM06");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("CHU Strasbourg");
        assertThat(result.get(0).getTotalNumberOfBeds()).isEqualTo(25);
        verify(hospitalRepository, times(1)).getBySpeciality("SM06");
    }

    @Test
    @DisplayName("findBySpecialty – retourne une liste vide pour un code inconnu")
    void findBySpecialty_shouldReturnEmptyList_whenCodeNotFound() {
        when(hospitalRepository.getBySpeciality("UNKNOWN")).thenReturn(List.of());

        List<Hospital> result = hospitalFetchService.findBySpecialty("UNKNOWN");

        assertThat(result).isEmpty();
        verify(hospitalRepository, times(1)).getBySpeciality("UNKNOWN");
    }

    @Test
    @DisplayName("findBySpecialty – retourne plusieurs hôpitaux pour une même spécialité")
    void findBySpecialty_shouldReturnMultipleHospitals() {
        when(hospitalRepository.getBySpeciality("SM06")).thenReturn(List.of(cardiology, oncology));

        List<Hospital> result = hospitalFetchService.findBySpecialty("SM06");

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Hospital::getName)
                .containsExactlyInAnyOrder("CHU Strasbourg", "Hôpital Civil");
    }

    @Test
    @DisplayName("findBySpecialty – délègue bien au repository (pas de logique parasite)")
    void findBySpecialty_shouldDelegateToRepository() {
        when(hospitalRepository.getBySpeciality(anyString())).thenReturn(List.of());

        hospitalFetchService.findBySpecialty("SM10");

        verify(hospitalRepository).getBySpeciality("SM10");
        verifyNoMoreInteractions(hospitalRepository);
    }
}
