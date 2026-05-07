package com.openclassrooms.micro_bed_availbility.infra.model;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "bed",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"hospital_id", "speciality_id"}) // une seule entrée par couple hôpital/spécialité
        }
)
@Data
public class BedsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBeds;

    @ManyToOne
    @JoinColumn(name = "hospital_id", nullable = false)
    private HospitalEntity hospital;

    @ManyToOne
    @JoinColumn(name = "speciality_id", nullable = false)
    private SpecialityEntity speciality;

    @Column(nullable = false)
    private int totalBeds;       // nombre total de lits

    @Column(nullable = false)
    private int bookedBeds;
}
