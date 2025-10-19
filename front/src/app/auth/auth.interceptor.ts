import {
  HttpInterceptorFn,
  HttpErrorResponse,
  HttpRequest,
} from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from './auth.service';

export const authInterceptor: HttpInterceptorFn = (req: any, next: any) => {
  const auth = inject(AuthService);

  if (auth.token) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${auth.token}`,
      },
    });
  }

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status != 401) {
        return throwError(() => error);
      }

      return auth.refreshToken().pipe(
        switchMap((newToken: string | null) => {
          if (!newToken) {
            return throwError(() => error);
          }

          return next(
            req.clone({
              setHeaders: {
                Authorization: `Bearer ${auth.token}`,
              },
            })
          );
        })
      );
    })
  );
};
