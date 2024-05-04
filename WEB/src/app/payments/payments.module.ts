import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PaymentsRoutingModule } from './payments-routing.module';
import { PaymentsGatewayComponent } from './payments-gateway/payments-gateway.component';


@NgModule({
  declarations: [
    PaymentsGatewayComponent
  ],
  imports: [
    CommonModule,
    PaymentsRoutingModule
  ]
})
export class PaymentsModule { }
