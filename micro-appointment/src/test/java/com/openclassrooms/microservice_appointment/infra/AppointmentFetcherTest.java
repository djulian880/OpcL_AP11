package com.openclassrooms.microservice_appointment.infra;

import com.openclassrooms.microservice_appointment.infra.AppointmentFetcher;
import com.openclassrooms.microservice_appointment.infra.entity.Appointment;
import com.openclassrooms.microservice_appointment.infra.repository.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AppointmentFetcherTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private AppointmentFetcher appointmentFetcher;

    private Appointment entityAppointment1;
    private Appointment entityAppointment2;

    @BeforeEach
    void setUp() {
        entityAppointment1 = new Appointment();
        entityAppointment1.setId(1L);
        entityAppointment1.setFirstName("Jean");
        entityAppointment1.setLastName("Dupont");
        entityAppointment1.setSpeciality("CARDIO");
        entityAppointment1.setHospital("CHU Lyon");
        entityAppointment1.setEntranceDate(new Date(1718400000000L)); // 2024-06-15
        entityAppointment1.setLeavingDate(new Date(1718659200000L));  // 2024-06-18

        entityAppointment2 = new Appointment();
        entityAppointment2.setId(2L);
        entityAppointment2.setFirstName("Marie");
        entityAppointment2.setLastName("Martin");
        entityAppointment2.setSpeciality("CARDIO");
        entityAppointment2.setHospital("CHU Lyon");
        entityAppointment2.setEntranceDate(new Date(1718400000000L));
        entityAppointment2.setLeavingDate(new Date(1718745600000L));
    }

    @Test
    @DisplayName("Doit mapper correctement les entités JPA vers les objets du domaine")
    void getBySpecialityAndHospitalAndDate_shouldMapEntityToDomain() {
        // Arrange
        String specialityCode = "CARDIO";
        String hospitalName = "CHU Lyon";
        String date = "2024-06-15";
        when(appointmentRepository.findBySpecialityAndHospitalAndDate(specialityCode, hospitalName, date))
                .thenReturn(List.of(entityAppointment1));

        // Act
        List<com.openclassrooms.microservice_appointment.domain.fetch.Appointment> result =
                appointmentFetcher.getBySpecialityAndHospitalAndDate(specialityCode, hospitalName, date);

        // Assert
        assertThat(result).hasSize(1);

        com.openclassrooms.microservice_appointment.domain.fetch.Appointment domainAppointment = result.get(0);
        assertThat(domainAppointment.getFirstName()).isEqualTo(entityAppointment1.getFirstName());
        assertThat(domainAppointment.getLastName()).isEqualTo(entityAppointment1.getLastName());
        assertThat(domainAppointment.getSpeciality()).isEqualTo(entityAppointment1.getSpeciality());
        assertThat(domainAppointment.getHospital()).isEqualTo(entityAppointment1.getHospital());
        assertThat(domainAppointment.getEntranceDate()).isEqualTo(entityAppointment1.getEntranceDate());
        assertThat(domainAppointment.getLeavingDate()).isEqualTo(entityAppointment1.getLeavingDate());
    }

    @Test
    @DisplayName("Doit retourner autant d'objets domaine que d'entités reçues")
    void getBySpecialityAndHospitalAndDate_shouldReturnAllMappedResults() {
        // Arrange
        String specialityCode = "CARDIO";
        String hospitalName = "CHU Lyon";
        String date = "2024-06-15";
        when(appointmentRepository.findBySpecialityAndHospitalAndDate(specialityCode, hospitalName, date))
                .thenReturn(List.of(entityAppointment1, entityAppointment2));

        // Act
        List<com.openclassrooms.microservice_appointment.domain.fetch.Appointment> result =
                appointmentFetcher.getBySpecialityAndHospitalAndDate(specialityCode, hospitalName, date);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFirstName()).isEqualTo("Jean");
        assertThat(result.get(1).getFirstName()).isEqualTo("Marie");
    }

    @Test
    @DisplayName("Doit retourner une liste vide si le repository ne trouve rien")
    void getBySpecialityAndHospitalAndDate_shouldReturnEmptyList_whenRepositoryReturnsNothing() {
        // Arrange
        when(appointmentRepository.findBySpecialityAndHospitalAndDate(any(), any(), any()))
                .thenReturn(List.of());

        // Act
        List<com.openclassrooms.microservice_appointment.domain.fetch.Appointment> result =
                appointmentFetcher.getBySpecialityAndHospitalAndDate("NEURO", "CHU Paris", "2024-01-01");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Doit appeler le repository avec les bons paramètres")
    void getBySpecialityAndHospitalAndDate_shouldCallRepositoryWithCorrectParams() {
        // Arrange
        String specialityCode = "PNEUMO";
        String hospitalName = "Hôpital Saint-Louis";
        String date = "2024-09-10";
        when(appointmentRepository.findBySpecialityAndHospitalAndDate(specialityCode, hospitalName, date))
                .thenReturn(List.of());

        // Act
        appointmentFetcher.getBySpecialityAndHospitalAndDate(specialityCode, hospitalName, date);

        // Assert
        verify(appointmentRepository, times(1))
                .findBySpecialityAndHospitalAndDate(specialityCode, hospitalName, date);
        verifyNoMoreInteractions(appointmentRepository);
    }
}