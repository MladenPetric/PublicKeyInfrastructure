import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  username = '';
  password = '';
  role = '';
  userId = '';
  organization = '';

  constructor(private router: Router, private auth: AuthService) {}

  onLogin() {
    this.auth
      .login({
        email: this.username,
        password: this.password,
      })
      .subscribe({
        next: (_) => {
          const user = this.auth.user;
          // ...
        },
        error: console.log,
      });
  }
}
