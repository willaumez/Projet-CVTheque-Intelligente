import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TemplateScreensComponent } from './template-screens.component';

describe('TemplateScreensComponent', () => {
  let component: TemplateScreensComponent;
  let fixture: ComponentFixture<TemplateScreensComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TemplateScreensComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(TemplateScreensComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
