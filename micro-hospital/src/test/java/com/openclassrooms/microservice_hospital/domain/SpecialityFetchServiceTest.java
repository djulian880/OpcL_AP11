package com.openclassrooms.microservice_hospital.domain;

import com.openclassrooms.microservice_hospital.domain.fetch.ISpecialityRepository;
import com.openclassrooms.microservice_hospital.domain.fetch.SpecialityFetchService;
import com.openclassrooms.microservice_hospital.infra.entity.Speciality;
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
@DisplayName("Tests unitaires – SpecialityFetchService")
class SpecialityFetchServiceTest {

    @Mock
    private ISpecialityRepository specialityRepository;

    @InjectMocks
    private SpecialityFetchService specialityFetchService;

    private Speciality cardio;
    private Speciality onco;

    @BeforeEach
    void setUp() {
        cardio = new Speciality();
        cardio.setCode("SM06");
        cardio.setName("Cardiologie");

        onco = new Speciality();
        onco.setCode("SM10");
        onco.setName("Oncologie");
    }

    @Test
    @DisplayName("getAll – retourne toutes les spécialités disponibles")
    void getAll_shouldReturnAllSpecialities() {
        when(specialityRepository.getAll()).thenReturn(List.of(cardio, onco));

        List<Speciality> result = specialityFetchService.getAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Speciality::getCode)
                .containsExactlyInAnyOrder("SM06", "SM10");
        verify(specialityRepository, times(1)).getAll();
    }

    @Test
    @DisplayName("getAll – retourne une liste vide si aucune spécialité en base")
    void getAll_shouldReturnEmptyList_whenNoSpecialities() {
        when(specialityRepository.getAll()).thenReturn(List.of());

        List<Speciality> result = specialityFetchService.getAll();

        assertThat(result).isEmpty();
        verify(specialityRepository, times(1)).getAll();
    }

    @Test
    @DisplayName("getAll – délègue uniquement au repository")
    void getAll_shouldDelegateToRepository() {
        when(specialityRepository.getAll()).thenReturn(List.of());

        specialityFetchService.getAll();

        verify(specialityRepository).getAll();
        verifyNoMoreInteractions(specialityRepository);
    }
}
