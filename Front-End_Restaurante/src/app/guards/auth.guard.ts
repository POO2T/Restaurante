import { Injectable, inject } from '@angular/core';
import { Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard {
  private authService = inject(AuthService);
  private router = inject(Router);

  // Angular will call canActivate(route, state).
  // We accept optional route data.role to require specific roles (e.g. 'FUNCIONARIO' or 'ADMIN').
  canActivate(route?: ActivatedRouteSnapshot, state?: RouterStateSnapshot): boolean {
  const requiredRole = route?.data?.['role'] as 'FUNCIONARIO' | 'CLIENTE' | 'ADMIN' | undefined;

    // Not authenticated -> redirect to selector
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/seletor-login']);
      return false;
    }

    // If no specific role required, any authenticated user passes
    if (!requiredRole) return true;

    // Role-specific checks
    if (requiredRole === 'FUNCIONARIO') {
      if (this.authService.isFuncionario()) return true;
      this.router.navigate(['/login-funcionario']);
      return false;
    }

    if (requiredRole === 'ADMIN') {
      if (this.authService.isAdmin()) return true;
      this.router.navigate(['/funcionario/mesas']);
      return false;
    }

    // default deny
    this.router.navigate(['/seletor-login']);
    return false;
  }

  canActivateFuncionario(): boolean {
    if (this.authService.isAuthenticated() && this.authService.isFuncionario()) {
      return true;
    }
    
    this.router.navigate(['/login-funcionario']);
    return false;
  }

  canActivateAdmin(): boolean {
    if (this.authService.isAuthenticated() && this.authService.isAdmin()) {
      return true;
    }
    
    this.router.navigate(['/']);
    return false;
  }
}