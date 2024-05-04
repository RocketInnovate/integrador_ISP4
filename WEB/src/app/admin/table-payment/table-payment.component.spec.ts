import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TablePaymentComponent } from './table-payment.component';

describe('TablePaymentComponent', () => {
  let component: TablePaymentComponent;
  let fixture: ComponentFixture<TablePaymentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TablePaymentComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TablePaymentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
