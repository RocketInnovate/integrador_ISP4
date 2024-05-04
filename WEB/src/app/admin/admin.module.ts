import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AdminRoutingModule } from './admin-routing.module';
import { DashboardComponent } from './dashboard/dashboard.component';
import { TableUserComponent } from './table-user/table-user.component';
import { TablePaymentComponent } from './table-payment/table-payment.component';
import { TableMessageComponent } from './table-message/table-message.component';
import { MainComponent } from './main/main.component';
import { BodyComponent } from './body/body.component';



@NgModule({
  declarations: [
    DashboardComponent,
    TableUserComponent,
    TablePaymentComponent,
    TableMessageComponent,
    MainComponent,
    BodyComponent,
    
  ],
  imports: [
    CommonModule,
    AdminRoutingModule
  ]
})
export class AdminModule { }
