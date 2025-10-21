import { Injectable, signal } from '@angular/core';
import { Observable, BehaviorSubject, tap, catchError, of } from 'rxjs';
import { ApiService } from '../api';
import { LoginRequest, LoginResponse, RegisterClienteRequest, RegisterFuncionarioRequest } from '../../models/auth.model';
import { Usuario } from '../../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<Usuario | null>(null);
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  
  // Signals para reactive programming
  currentUser = signal<Usuario | null>(null);
  isAuthenticated = signal<boolean>(false);
  userType = signal<'CLIENTE' | 'FUNCIONARIO' | null>(null);

  constructor(private apiService: ApiService) {
    this.checkAuthStatus();
  }

  // Login de Cliente
  loginCliente(credentials: LoginRequest): Observable<LoginResponse> {
    return this.apiService.post<LoginResponse>('/auth/cliente/login', credentials)
      .pipe(
        tap(response => this.handleLoginSuccess(response, 'CLIENTE')),
        catchError(this.handleError)
      );
  }

  // Login de Funcionário  
  loginFuncionario(credentials: LoginRequest): Observable<LoginResponse> {
    return this.apiService.post<LoginResponse>('/auth/funcionario/login', credentials)
      .pipe(
        tap(response => this.handleLoginSuccess(response, 'FUNCIONARIO')),
        catchError(this.handleError)
      );
  }

  // Registro de Cliente
  registerCliente(userData: RegisterClienteRequest): Observable<any> {
    return this.apiService.post('/auth/cliente/register', userData)
      .pipe(catchError(this.handleError));
  }

  // Registro de Funcionário (só admin)
  registerFuncionario(userData: RegisterFuncionarioRequest): Observable<any> {
    return this.apiService.post('/auth/funcionario/register', userData)
      .pipe(catchError(this.handleError));
  }

  // Logout
  logout(): void {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('user_data');
    localStorage.removeItem('user_type');
    
    this.currentUser.set(null);
    this.isAuthenticated.set(false);
    this.userType.set(null);
    
    this.currentUserSubject.next(null);
    this.isAuthenticatedSubject.next(false);
  }

  // Verificar se usuário está autenticado
  private checkAuthStatus(): void {
    const token = localStorage.getItem('auth_token');
    const userData = localStorage.getItem('user_data');
    const userType = localStorage.getItem('user_type') as 'CLIENTE' | 'FUNCIONARIO';

    if (token && userData) {
      const user = JSON.parse(userData);
      this.currentUser.set(user);
      this.isAuthenticated.set(true);
      this.userType.set(userType);
      
      this.currentUserSubject.next(user);
      this.isAuthenticatedSubject.next(true);
    }
  }

  // Handle login success
  private handleLoginSuccess(response: LoginResponse, type: 'CLIENTE' | 'FUNCIONARIO'): void {
    localStorage.setItem('auth_token', response.token);
    localStorage.setItem('user_data', JSON.stringify(response.usuario));
    localStorage.setItem('user_type', type);
    
    this.currentUser.set(response.usuario);
    this.isAuthenticated.set(true);
    this.userType.set(type);
    
    this.currentUserSubject.next(response.usuario);
    this.isAuthenticatedSubject.next(true);
  }

  // Handle errors
  private handleError(error: any): Observable<never> {
    console.error('Auth error:', error);
    throw error;
  }

  // Getters para compatibilidade com RxJS
  get currentUser$(): Observable<Usuario | null> {
    return this.currentUserSubject.asObservable();
  }

  get isAuthenticated$(): Observable<boolean> {
    return this.isAuthenticatedSubject.asObservable();
  }

  // Verificar se é admin
  isAdmin(): boolean {
    const user = this.currentUser();
    return this.userType() === 'FUNCIONARIO' && 
           (user as any)?.tipoFuncionario === 'GERENTE';
  }

  // Verificar se é funcionário
  isFuncionario(): boolean {
    return this.userType() === 'FUNCIONARIO';
  }

  // Verificar se é cliente
  isCliente(): boolean {
    return this.userType() === 'CLIENTE';
  }
}