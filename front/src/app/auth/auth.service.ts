import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, tap, map, catchError, of } from 'rxjs';
import { environment } from '../../../environment/environment';


export interface User {
  email: string
  roles: string[]
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private userSubject = new BehaviorSubject<User | null>(null)

  public user$ = userSubject.asObservable()

  public get user(): User | null {
    return this.userSubject.value
  }

  private _token: string | null = null

  public get token() {
    return this._token
  }

  constructor(private jwtHelper: JwtHelperService, private http: HttpClient) {
    this.refreshToken().subscribe()
  }

  public login(request: {email: string, password: string}) {
    return this.http.post<{ accessToken: string }>(
      `${environment.apiUrl}/auth/login`,
      request,
      { withCredentials: true },
    ).pipe(
      tap(response => this.setAccessToken(response.accessToken))
      map(response => response.accessToken)
    )
  }

  public logout() {
    this.setAccessToken(null)
  }

  public refreshToken() {
    return this.http.post<{ accessToken: string }>(
      `${environment.apiUrl}/auth/refresh`,
      {},
      { withCredentials: true },
    ).pipe(
      tap(response => {
        this.setAccessToken(response.accessToken)
      }),
      map(response => response.accessToken),
      catchError(err => {
        this.logout()
        return of(null)
      })
    )
  }

  public whoAmI() {
    return this.http.get<User>(`${environment.apiUrl}/auth/whoami`);
  }

  private setAccessToken(token) {
    this._token = token

    if (token == null) {
      this.userSubject.next(null)
      return
    }

    try {
      const decoded = this.jwtHelper.decodeToken(token)
      if (!decoded) {
        throw new Error('')
      }

      const user = {
        email: decoded.email | decoded.sub | '',
        roles: decoded.roles | [],
      }
      this.userSubject.next(user)
    } catch {
      this.whoAmI().subscribe({
        next: user => this.userSubject.next(user),
        error: () => this.userSubject.next(null),
      });
    }
  }

}
