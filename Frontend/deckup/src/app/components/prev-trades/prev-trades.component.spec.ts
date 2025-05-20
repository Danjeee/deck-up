import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrevTradesComponent } from './prev-trades.component';

describe('PrevTradesComponent', () => {
  let component: PrevTradesComponent;
  let fixture: ComponentFixture<PrevTradesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PrevTradesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PrevTradesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
