import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoadFilesComponent } from './load-files.component';

describe('LoadFilesComponent', () => {
  let component: LoadFilesComponent;
  let fixture: ComponentFixture<LoadFilesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoadFilesComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(LoadFilesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
