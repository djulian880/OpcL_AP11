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
  bedVoid:Bed=new Bed();

  responseTime: number = 0;

  constructor(
      private specialityService: SpecialityService,
      private bedService: BedService,
      private cdr: ChangeDetectorRef
    ) {
    this.bedVoid.hospitalAddress="Pas d'adresse";
    this.bedVoid.hospitalName="Pas d'hopital";
    this.bedVoid.speciality="Pas de spécialité";

  }

  ngOnInit(): void {
    this.specialities$ = this.specialityService.specialities$;
    this.specialities$.pipe(takeUntil(this.destroy$)).subscribe((list) => {
      this.listSpecialities = list || []; // Gère le cas null/undefined
    });
  }

  findBed(): void {
    const startTime = Date.now();
    this.responseTime=0;


    this.bedService.findBed(this.selectedSpeciality, this.address).subscribe({
      next: (response: Bed) => {
        const responseTime = Date.now() - startTime;
        this.bed = response as Bed; // garde l'objet tel quel
        //console.log('Réponse reçue:', response);
        //console.log('this.bed:', this.bed);
        this.responseTime = responseTime;
        this.cdr.detectChanges();
      },
      error: (error) => {
        const responseTime = Date.now() - startTime;
        //console.error('Erreur:', error);
        this.responseTime = responseTime;
        this.bed=this.bedVoid;
        this.cdr.detectChanges();
        //this.response = 'Erreur lors de la demande.';
      },
    });
  }

}
