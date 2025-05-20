import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrevGamesComponent } from './prev-games.component';

describe('PrevGamesComponent', () => {
  let component: PrevGamesComponent;
  let fixture: ComponentFixture<PrevGamesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PrevGamesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PrevGamesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
