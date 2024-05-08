import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/auth/auth.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  isMenuOpen = false;
  isLogged = false; // Maintain existing login state variable

  constructor(public authService: AuthService) {
    this.authService.getIsLogged().subscribe(logged => {
      this.isLogged = logged;
    });
  }

  ngOnInit() {
    // Check for initial state based on media query (optional)
    if (window.innerWidth < 768) {
      this.isMenuOpen = true; // Set menu open for smaller screens initially
    }
  }

  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
  }

  logOut() {
    this.authService.logOut();
  }
}