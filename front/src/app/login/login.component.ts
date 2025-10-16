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

  constructor(private router: Router) {}

  onLogin() {   
    localStorage.setItem('userRole', 'ADMIN'); // OVO IDE DINAMICKI U ZAVISNOSTI KO SE ULOGUJE
    this.router.navigate(['/admin']);  // OVO IDE DINAMICKI U ZAVISNOSTI KO SE ULOGUJE
  }

}
