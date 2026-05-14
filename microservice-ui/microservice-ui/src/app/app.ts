import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HospitalFinder } from './hospital-finder/hospital-finder';

@Component({
  selector: 'app-root',
  imports: [HospitalFinder],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly title = signal('microservice-ui');
}
