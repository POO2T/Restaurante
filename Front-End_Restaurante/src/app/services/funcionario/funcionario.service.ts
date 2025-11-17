import { inject, Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';

import { ApiService } from '../api';
import { RegisterFuncionarioRequest } from '../../models/auth.model';
import { Usuario } from '../../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class FuncionarioService {
  private apiService = inject(ApiService);

  getFuncionarios(): Observable<any> {
    return this.apiService.get('/funcionarios');
  }

  registerFuncionario(userData: RegisterFuncionarioRequest): Observable<Usuario> {
    // ENDPOINT CORRETO: /api/funcionarios
    return this.apiService
      .post<Usuario>('/funcionarios', userData) // Ajustei para /api/funcionarios, verifique seu backend
      .pipe(catchError(this.handleError));
  }

  updateFuncionario(id: number, userData: Partial<RegisterFuncionarioRequest>): Observable<Usuario> {
    return this.apiService
      .put<Usuario>(`/funcionarios/${id}`, userData)
      .pipe(catchError(this.handleError));
  }

  deleteFuncionario(id: number): Observable<void> {
    return this.apiService
      .delete<void>(`/funcionarios/${id}`)
      .pipe(catchError(this.handleError));
  }

  
  // --- TRATAR ERROS ---
  private handleError(error: unknown): Observable<never> {
    console.error('Auth error:', error);
    return throwError(() => error);
  }
}