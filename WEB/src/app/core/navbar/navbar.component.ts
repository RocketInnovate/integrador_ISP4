import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/auth/auth.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  isMenuOpen = false;
  isLoggedTest = true;
  isLogged = true;

  constructor(public authService: AuthService) {
    this.authService.getIsLogged().subscribe(logged => {
      this.isLogged = logged;
    });
  }

  ngOnInit() {
    
    if (window.innerWidth < 768) {
      this.isMenuOpen = true; 
    }
  }

  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
  }

  logOut() {
    this.authService.logOut();
  }
}