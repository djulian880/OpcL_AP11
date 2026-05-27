import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HospitalFinder } from './hospital-finder';

describe('HospitalFinder', () => {
  let component: HospitalFinder;
  let fixture: ComponentFixture<HospitalFinder>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HospitalFinder],
    }).compileComponents();

    fixture = TestBed.createComponent(HospitalFinder);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
