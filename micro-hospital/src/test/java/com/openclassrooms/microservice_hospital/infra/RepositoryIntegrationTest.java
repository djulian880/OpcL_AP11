package com.openclassrooms.microservice_hospital.infra;

import com.openclassrooms.microservice_hospital.infra.entity.Bed;
import com.openclassrooms.microservice_hospital.infra.entity.Hospital;
import com.openclassrooms.microservice_hospital.infra.entity.Speciality;
import com.openclassrooms.microservice_hospital.infra.repository.BedRepository;
import com.openclassrooms.microservice_hospital.infra.repository.HospitalRepository;
import com.openclassrooms.microservice_hospital.infra.repository.SpecialityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;


import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests d'intégration JPA utilisant une base H2 en mémoire.
 *
 * Désactive HospitalSpecialityLoader via la propriété spring.jpa.hibernate.ddl-auto=create-drop
 * et un profil de test qui ne charge pas le @PostConstruct de chargement FHIR.
 *
 * Ajoutez dans src/test/resources/application-test.properties :
 *   spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
 *   spring.datasource.driver-class-name=org.h2.Driver
 *   spring.jpa.hibernate.ddl-auto=create-drop
 *   spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
 */
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("Tests d'intégration JPA – Repositories")
class RepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BedRepository bedRepository;

    @Autowired
    private SpecialityRepository specialityRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    private Speciality cardio;
    private Hospital chuStrasbourg;

    @BeforeEach
    void setUp() {
        cardio = new Speciality();
        cardio.setCode("SM06");
        cardio.setName("Cardiologie");
        entityManager.persist(cardio);

        Speciality onco = new Speciality();
        onco.setCode("SM10");
        onco.setName("Oncologie");
        entityManager.persist(onco);

        chuStrasbourg = new Hospital();
        chuStrasbourg.setName("CHU Strasbourg");
        chuStrasbourg.setAddress("1 Rue Molière Strasbourg");
        chuStrasbourg.getSpecialities().add(cardio);
        entityManager.persist(chuStrasbourg);

        Bed bed = new Bed();
        bed.setHospital(chuStrasbourg);
        bed.setSpeciality(cardio);
        bed.setTotalNumberOfBeds(25);
        entityManager.persist(bed);

        entityManager.flush();
    }

    // ────────────────────────────────────────────────────────────
    // BedRepository
    // ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("BedRepository.findBySpecialityCode – retourne les lits pour un code valide")
    void findBySpecialityCode_shouldReturnBeds_whenCodeExists() {
        List<Bed> beds = bedRepository.findBySpecialityCode("SM06");

        assertThat(beds).hasSize(1);
        assertThat(beds.get(0).getTotalNumberOfBeds()).isEqualTo(25);
        assertThat(beds.get(0).getHospital().getName()).isEqualTo("CHU Strasbourg");
        assertThat(beds.get(0).getSpeciality().getCode()).isEqualTo("SM06");
    }

    @Test
    @DisplayName("BedRepository.findBySpecialityCode – retourne liste vide pour un code inconnu")
    void findBySpecialityCode_shouldReturnEmpty_whenCodeNotFound() {
        List<Bed> beds = bedRepository.findBySpecialityCode("INVALID");

        assertThat(beds).isEmpty();
    }

    @Test
    @DisplayName("BedRepository.findBySpecialityCode – ne retourne pas les lits d'autres spécialités")
    void findBySpecialityCode_shouldNotReturnBedsOfOtherSpecialities() {
        List<Bed> beds = bedRepository.findBySpecialityCode("SM10");

        assertThat(beds).isEmpty();
    }

    // ────────────────────────────────────────────────────────────
    // SpecialityRepository
    // ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("SpecialityRepository.findByName – retourne la spécialité si elle existe")
    void findByName_shouldReturnSpeciality_whenExists() {
        Optional<Speciality> result = specialityRepository.findByName("Cardiologie");

        assertThat(result).isPresent();
        assertThat(result.get().getCode()).isEqualTo("SM06");
    }

    @Test
    @DisplayName("SpecialityRepository.findByName – retourne Optional vide si absente")
    void findByName_shouldReturnEmpty_whenNotFound() {
        Optional<Speciality> result = specialityRepository.findByName("Dermatologie");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("SpecialityRepository.findAll – retourne toutes les spécialités")
    void findAll_shouldReturnAllSpecialities() {
        List<Speciality> all = specialityRepository.findAll();

        assertThat(all).hasSizeGreaterThanOrEqualTo(2);
        assertThat(all).extracting(Speciality::getCode)
                .contains("SM06", "SM10");
    }

    // ────────────────────────────────────────────────────────────
    // Entités – contraintes de persistance
    // ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Hospital – persiste et retrouve correctement ses champs")
    void hospital_shouldPersistAndRetrieveFields() {
        Optional<Hospital> found = hospitalRepository.findById(chuStrasbourg.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("CHU Strasbourg");
        assertThat(found.get().getAddress()).isEqualTo("1 Rue Molière Strasbourg");
    }

    @Test
    @DisplayName("Speciality – le code et le nom sont bien uniques (contrainte DB)")
    void speciality_codeMustBeUnique() {
        Speciality duplicate = new Speciality();
        duplicate.setCode("SM06");   // code déjà en base
        duplicate.setName("Duplicate");

        org.junit.jupiter.api.Assertions.assertThrows(
                Exception.class,
                () -> {
                    entityManager.persist(duplicate);
                    entityManager.flush();
                }
        );
    }
}
