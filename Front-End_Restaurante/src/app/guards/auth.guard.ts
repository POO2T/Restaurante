import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard {
  private authService = inject(AuthService);
  private router = inject(Router);

  canActivate(): boolean {
    if (this.authService.isAuthenticated()) {
      return true;
    }
    
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