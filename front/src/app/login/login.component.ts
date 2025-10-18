import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  username = '';
  password = '';
  role = '';
  userId = '';
  organization = '';

  constructor(private router: Router) {}

  onLogin() {   
    this.role = 'ADMIN'; // OVO IDE DINAMICKI U ZAVISNOSTI KO SE ULOGUJE
    this.userId = 'b6c57d7b-fb9e-4a47-9b71-b1b2f3f4b5b6'; // OVO IDE DINAMICKI U ZAVISNOSTI KO SE ULOGUJE
    this.organization = 'MAP'; // OVO IDE DINAMICKI U ZAVISNOSTI KO SE ULOGUJE
    
    localStorage.setItem('userRole', this.role);
    localStorage.setItem('userId', this.userId);
    localStorage.setItem('organization', this.organization);

    if (this.role === 'ADMIN') {
      this.router.navigate(['/admin']);
    } else if (this.role === 'USER') {
      this.router.navigate(['/user']);
    } else if (this.role === 'CA') {
      this.router.navigate(['/ca']);
    }
  }

}
