import { Component } from '@angular/core';
import { AuthService } from 'src/app/auth/auth.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {
  isLogged=false;
  constructor(public authService: AuthService){
    this.authService.getIsLogged().subscribe(logged => {
      this.isLogged = logged;
    });
  }
  logOut(){
    this.authService.logOut()
  }
}
