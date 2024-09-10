import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfileCriteriaComponent } from './profile-criteria.component';

describe('ProfileCriteriaComponent', () => {
  let component: ProfileCriteriaComponent;
  let fixture: ComponentFixture<ProfileCriteriaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ProfileCriteriaComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ProfileCriteriaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
