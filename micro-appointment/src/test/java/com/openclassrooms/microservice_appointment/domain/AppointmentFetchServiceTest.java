package com.openclassrooms.microservice_appointment.domain;

import com.openclassrooms.microservice_appointment.domain.fetch.Appointment;
import com.openclassrooms.microservice_appointment.domain.fetch.AppointmentFetchService;
import com.openclassrooms.microservice_appointment.domain.fetch.IAppointmentRepository;
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
class AppointmentFetchServiceTest {

    @Mock
    private IAppointmentRepository appointmentRepository;

    @InjectMocks
    private AppointmentFetchService appointmentFetchService;

    private Appointment appointment1;
    private Appointment appointment2;

    @BeforeEach
    void setUp() {
        appointment1 = new Appointment();
        appointment1.setFirstName("Jean");
        appointment1.setLastName("Dupont");
        appointment1.setSpeciality("CARDIO");
        appointment1.setHospital("CHU Lyon");
        appointment1.setEntranceDate(new Date());
        appointment1.setLeavingDate(new Date());

        appointment2 = new Appointment();
        appointment2.setFirstName("Marie");
        appointment2.setLastName("Martin");
        appointment2.setSpeciality("CARDIO");
        appointment2.setHospital("CHU Lyon");
        appointment2.setEntranceDate(new Date());
        appointment2.setLeavingDate(new Date());
    }

    @Test
    @DisplayName("Doit retourner la liste des rendez-vous correspondant aux critères")
    void findBySpecialityAndHospitalAndDate_shouldReturnMatchingAppointments() {
        // Arrange
        String specialityCode = "CARDIO";
        String hospitalName = "CHU Lyon";
        String date = "2024-06-15";
        when(appointmentRepository.getBySpecialityAndHospitalAndDate(specialityCode, date))
                .thenReturn(List.of(appointment1, appointment2));

        // Act
        List<Appointment> result = appointmentFetchService.findBySpecialityAndHospitalAndDate(specialityCode, date);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(appointment1, appointment2);
        verify(appointmentRepository, times(1))
                .getBySpecialityAndHospitalAndDate(specialityCode, date);
    }

    @Test
    @DisplayName("Doit retourner une liste vide si aucun rendez-vous ne correspond")
    void findBySpecialityAndHospitalAndDate_shouldReturnEmptyList_whenNoMatch() {
        // Arrange
        String specialityCode = "NEURO";
        String hospitalName = "CHU Paris";
        String date = "2024-01-01";
        when(appointmentRepository.getBySpecialityAndHospitalAndDate(specialityCode, date))
                .thenReturn(List.of());

        // Act
        List<Appointment> result = appointmentFetchService.findBySpecialityAndHospitalAndDate(specialityCode, date);

        // Assert
        assertThat(result).isEmpty();
        verify(appointmentRepository, times(1))
                .getBySpecialityAndHospitalAndDate(specialityCode, date);
    }

    @Test
    @DisplayName("Doit déléguer exactement les paramètres reçus au repository")
    void findBySpecialityAndHospitalAndDate_shouldForwardExactParameters() {
        // Arrange
        String specialityCode = "ORTHO";
        String hospitalName = "Hôpital Lariboisière";
        String date = "2024-12-25";
        when(appointmentRepository.getBySpecialityAndHospitalAndDate(any(), any()))
                .thenReturn(List.of());

        // Act
        appointmentFetchService.findBySpecialityAndHospitalAndDate(specialityCode, date);

        // Assert
        verify(appointmentRepository).getBySpecialityAndHospitalAndDate(specialityCode, date);
        verifyNoMoreInteractions(appointmentRepository);
    }
}
