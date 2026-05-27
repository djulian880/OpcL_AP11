import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class BedService {

  private readonly apiUrl = 'http://localhost:8100/Bed';

  constructor(private http: HttpClient) {
  }


  findBed(speciality: string, address: string): Observable<any> {
    const params = new HttpParams()
      .set('speciality', speciality)
      .set('address', address);

    return this.http.get(`${this.apiUrl}/beds`, { params });
  }



}
