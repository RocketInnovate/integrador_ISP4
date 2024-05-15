import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MainComponent } from './home/main/main.component';


const routes: Routes = [
  {path: '', component:MainComponent},
  {path: 'auth',loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule)},
  {path: 'admin',loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule)},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
