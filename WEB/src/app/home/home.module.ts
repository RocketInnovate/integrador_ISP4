import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { HomeRoutingModule } from './home-routing.module';
import { MainComponent } from './main/main.component';
import { AboutUsComponent } from './about-us/about-us.component';
import { ContactComponent } from './contact/contact.component';
import { HeaderComponent } from './header/header.component';



@NgModule({
  declarations: [
    AboutUsComponent,
    ContactComponent,
    HeaderComponent,
    MainComponent
  ],
  imports: [
    CommonModule,
    HomeRoutingModule
  ]
})
export class HomeModule { }
