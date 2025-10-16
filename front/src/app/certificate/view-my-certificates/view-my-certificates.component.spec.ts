import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewMyCertificatesComponent } from './view-my-certificates.component';

describe('ViewMyCertificatesComponent', () => {
  let component: ViewMyCertificatesComponent;
  let fixture: ComponentFixture<ViewMyCertificatesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ViewMyCertificatesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ViewMyCertificatesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
