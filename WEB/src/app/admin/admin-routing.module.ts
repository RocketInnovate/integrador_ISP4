import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { TableUserComponent } from './table-user/table-user.component';
import { TablePaymentComponent } from './table-payment/table-payment.component';
import { TableMessageComponent } from './table-message/table-message.component';
import { MainComponent } from './main/main.component';

const routes: Routes = [
{
  path: '', component:MainComponent,
  children: [
    { path: 'user', component: TableUserComponent },
    { path: 'payments', component: TablePaymentComponent },
    { path: 'message', component: TableMessageComponent },
  ]
},

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule { }
