import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { filter } from 'rxjs';

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

    this.auth.login({
      email: this.username,
      password: this.password,
    }).subscribe({
      next: _ => console.log("Login successfull"),
      error: console.log
    })

    this.auth.user$.pipe(
      filter(user => !!user)
    ).subscribe(user => {
      this.role = user.role

      if (this.role === 'ROLE_ADMIN') {
          this.router.navigate(['/admin']);
        } else if (this.role === 'ROLE_SIMPSON') {
          this.router.navigate(['/user']);
        } else if (this.role === 'ROLE_CA') {
          this.router.navigate(['/ca']);
        }
    })

  }


}
