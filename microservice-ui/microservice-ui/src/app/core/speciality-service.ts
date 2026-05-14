import { Injectable } from '@angular/core';
import { Speciality } from './speciality';
import { BehaviorSubject, tap } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class SpecialityService {
  specialitiesSubject = new BehaviorSubject<Speciality[]>([]);
  specialities$ = this.specialitiesSubject.asObservable();
  private readonly apiUrl = 'http://localhost:8100/Hospital';

  constructor(private http: HttpClient) {
    this.fetchSpecialities();
  }

  fetchSpecialities(): void {
    //console.log("VariableService: Chargement des types primitifs");
    this.http
      .get<Speciality[]>(this.apiUrl + '/specialities/all')
      .pipe(
        tap((specialities:Speciality[]) =>
          this.specialitiesSubject.next(
            specialities.map((element: any) => Object.assign(new Speciality(), element)),
          ),
        ), // Mettre à jour le BehaviorSubject
      )
      .subscribe();
    //console.log("VariableService: Chargement des types primitifs:"+this.listTypes.length);
  }

}
