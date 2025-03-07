import { TestBed } from '@angular/core/testing';

import { ApacheService } from './apache.service';

describe('ApacheService', () => {
  let service: ApacheService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ApacheService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
