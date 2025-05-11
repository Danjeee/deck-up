import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TraderoomComponent } from './traderoom.component';

describe('TraderoomComponent', () => {
  let component: TraderoomComponent;
  let fixture: ComponentFixture<TraderoomComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TraderoomComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TraderoomComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
