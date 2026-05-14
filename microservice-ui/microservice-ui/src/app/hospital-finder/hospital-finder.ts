import { Component } from '@angular/core';
import { Speciality } from '../core/speciality';
import { SpecialityService } from '../core/speciality-service';
import { Observable, Subject, takeUntil } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { BedService } from '../core/bed-service';
import { Bed } from '../core/bed';
import { NgIf } from '@angular/common';
import { ChangeDetectorRef } from '@angular/core';


@Component({
  selector: 'app-hospital-finder',
  imports: [FormsModule, NgIf],
  templateUrl: './hospital-finder.html',
  styleUrl: './hospital-finder.css',
})
export class HospitalFinder {
  specialities$!: Observable<Speciality[]>;
  private destroy$ = new Subject<void>();
  listSpecialities!: Speciality[];

  selectedSpeciality!: string;
  address!: string;

  bed!: Bed;

  constructor(
    private specialityService: SpecialityService,
    private bedService: BedService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.specialities$ = this.specialityService.specialities$;
    this.specialities$.pipe(takeUntil(this.destroy$)).subscribe((list) => {
      this.listSpecialities = list || []; // Gère le cas null/undefined
    });
  }

  findBed(): void {
    this.bedService.findBed(this.selectedSpeciality, this.address).subscribe({
      next: (response: Bed) => {
        this.bed = response as Bed; // garde l'objet tel quel
        console.log('Réponse reçue:', response);
        console.log('this.bed:', this.bed);
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Erreur:', error);
        this.cdr.detectChanges();
        //this.response = 'Erreur lors de la demande.';
      },
    });
  }
}
