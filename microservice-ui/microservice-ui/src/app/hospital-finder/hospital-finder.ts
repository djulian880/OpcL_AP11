import { Component } from '@angular/core';
import { Speciality } from '../core/speciality';
import { SpecialityService } from '../core/speciality-service';
import { Observable, Subject, takeUntil } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { BedService } from '../core/bed-service';
import { Bed } from '../core/bed';
import { NgOptimizedImage } from '@angular/common';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-hospital-finder',
  imports: [FormsModule, NgOptimizedImage],
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
  bedVoid: Bed = new Bed();

  responseTime: number = 0;

  constructor(
    private specialityService: SpecialityService,
    private bedService: BedService,
    private cdr: ChangeDetectorRef,
  ) {
    this.bedVoid.hospitalAddress = "Pas d'adresse";
    this.bedVoid.hospitalName = "Pas d'hopital";
    this.bedVoid.speciality = 'Pas de spécialité';
  }

  ngOnInit(): void {
    this.specialities$ = this.specialityService.specialities$;
    this.specialities$.pipe(takeUntil(this.destroy$)).subscribe((list) => {
      this.listSpecialities = list || []; // Gère le cas null/undefined
    });
  }

  findBed(): void {
    const startTime = Date.now();
    this.responseTime = 0;

    this.bedService.findBed(this.selectedSpeciality, this.address).subscribe({
      next: (response: Bed) => {
        this.bed = response as Bed;
        this.responseTime = Date.now() - startTime;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Erreur:', error);
        this.responseTime = Date.now() - startTime;
        this.bed = this.bedVoid;
        this.cdr.detectChanges();
      },
    });
  }
}
