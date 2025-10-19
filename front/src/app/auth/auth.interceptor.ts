import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from './auth.service';
import { HttpErrorResponse } from '@angular/common/http';


function withAuthorization(req, token) {
  return req.clone({
    setHeaders: {
      Authorization: `Bearer ${auth.token}`
    }
  })
}


export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService)

  if (auth.token) {
    req = withAuthorization(req, auth.token)
  }

  return next(req).pipe(
    catchError((error) => {
      if (error.status != 401) {
        return throwError(() => error)
      }

      return auth.refreshToken().pipe(
        switchMap(newToken => {
          if (!newToken) {
            return throwError(() => error)
          }

          return next(withAuthorization(req, newToken))
        })
      )
    })
  )
};
