import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, tap, map, catchError, of } from 'rxjs';
import { environment } from '../../enviroment/enviroment';

export interface User {
  email: string;
  password: string;
  name: string; 
  surname: string;
  organization: string;
  role: 'ROLE_ADMIN' | 'ROLE_CA' | 'ROLE_SIMPSON' | null;
  status: 'ACTIVE' | 'INACTIVE'
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private userSubject = new BehaviorSubject<User | null>(null);

  public user$ = this.userSubject.asObservable();

  public get user(): User | null {
    return this.userSubject.value;
  }

  private _token: string | null = null;

  public get token() {
    return this._token;
  }

  constructor(private http: HttpClient) {
    this.refreshToken().subscribe();
  }

  public login(request: { email: string; password: string }) {
    return this.http
      .post<{ accessToken: string }>(
        `${environment.apiUrl}/auth/login`,
        request,
        { withCredentials: true }
      )
      .pipe(
        tap((response) => this.setAccessToken(response.accessToken)),
        map((response) => response.accessToken)
      );
  }

  public logout() {
    this.setAccessToken(null);
  }

  public refreshToken() {
    return this.http
      .post<{ accessToken: string }>(
        `${environment.apiUrl}/auth/refresh`,
        {},
        { withCredentials: true }
      )
      .pipe(
        tap((response) => {
          this.setAccessToken(response.accessToken);
        }),
        map((response) => response.accessToken),
        catchError((err) => {
          this.logout();
          return of(null);
        })
      );
  }

  public whoAmI() {
    return this.http.get<User>(`${environment.apiUrl}/auth/whoami`);
  }

  private setAccessToken(token) {
    this._token = token;

    if (token == null) {
      this.userSubject.next(null);
      return;
    }

    this.whoAmI().subscribe({
        next: (user: User) => this.userSubject.next(user),
        error: () => this.userSubject.next(null),
      });
  }
}