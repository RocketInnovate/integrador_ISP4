import { Component, OnInit, Injectable } from '@angular/core';
import { AuthService } from 'src/app/auth/auth.service';
import { Observable, of, from } from 'rxjs';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
@Injectable({
  providedIn: 'root'
})
export class NavbarComponent implements OnInit {
  isMenuOpen = false;
  isLoggedTest = true;
  isLogged = true;
  user: any;
  isStaff = false;

  constructor(public authService: AuthService) {
    this.authService.getIsLogged().subscribe(logged => {
      this.isLogged = logged;

      if (logged) {
        const userData = localStorage.getItem('user');
        if (userData) {
          this.user = JSON.parse(userData);
          this.checkIsStaff(); // Check isStaff property on login
        }
      } else {
        this.user = null; // Clear user object on logout
      }
    });
  }

  ngOnInit() {
    const userData = localStorage.getItem('user');
    if (userData) {
      this.user = JSON.parse(userData);
      if (this.isLogged) { // Check isStaff property on initial load if logged in
        this.checkIsStaff();
      }
    }

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

  private checkIsStaff() {
    if (this.user && this.user.is_staff) {
      // Update navbar to display "este usuario es admin"
      console.log('Este usuario es admin');
      this.isStaff = true;
    } else {
      // Update navbar to display "este usuario es cliente"
      console.log('Este usuario es cliente');
      this.isStaff = false;
    }
  }
}
