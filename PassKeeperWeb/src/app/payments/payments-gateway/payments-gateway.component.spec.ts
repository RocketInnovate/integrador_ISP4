import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymentsGatewayComponent } from './payments-gateway.component';

describe('PaymentsGatewayComponent', () => {
  let component: PaymentsGatewayComponent;
  let fixture: ComponentFixture<PaymentsGatewayComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PaymentsGatewayComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PaymentsGatewayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
