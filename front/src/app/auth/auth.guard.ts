import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { inject } from '@angular/core';


export const authGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  const user = auth.user;
  if (!user?.roles) {
    router.navigate(['/login']).then();
    return false;
  }

  const allowedRoles: string[] | undefined = route.data?.['roles'];

  if (!allowedRoles || user.roles.some(role => allowedRoles.includes(role)))
    return true;

  router.navigate(['/unauthorized']).then();
  return false;
};
