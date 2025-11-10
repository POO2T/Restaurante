import { Injectable, signal, inject, effect } from '@angular/core';
import { Observable, tap, catchError, throwError } from 'rxjs'; // Importe throwError
import { signalToObservable } from '../../utils/signal-observable';
import { ApiService } from '../api'; // Seu serviço base para chamadas HTTP
import { StorageService } from '../storage.service';
import { LoginRequest, LoginResponse, RegisterClienteRequest, RegisterFuncionarioRequest } from '../../models/auth.model'; // Seus modelos
import { Usuario } from '../../models/user.model'; // Seu modelo base de usuário

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  // Signals para uma abordagem mais moderna e reativa no Angular
  currentUser = signal<Usuario | null>(null);
  isAuthenticated = signal<boolean>(false);
  userType = signal<'CLIENTE' | 'FUNCIONARIO' | null>(null);

  private apiService = inject(ApiService);
  private storage = new StorageService();

  // Effects can run side effects as signals change; keep a noop effect to retain reactivity
  private _noopSync = effect(() => {
    void this.currentUser();
    void this.isAuthenticated();
  });

  // Tenta carregar o estado de autenticação ao iniciar o serviço
  init() {
    if (this.storage.isBrowser()) {
      this.checkAuthStatus();
    }
  }

  // --- LOGIN UNIFICADO ---
  // Usado por ambas as páginas (login-cliente e login-funcionario)
  login(credentials: LoginRequest): Observable<LoginResponse> {
    const loginEndpoint = '/auth/login'; // SEU ENDPOINT UNIFICADO
    return this.apiService
      .post<LoginResponse>(loginEndpoint, credentials)
      .pipe(
        tap((response) => {
          console.log('Login success response:', response);

          if (!response) {
            throw new Error('Resposta de login inválida do servidor.');
          }

          // Determinar tipo de usuário baseado na resposta
          let tipo: 'CLIENTE' | 'FUNCIONARIO' = (response as any).tipoUsuario.toUpperCase() ?? null;
          
          if (!tipo) {
            const u = (response as any).dadosUsuario ?? (response as LoginResponse).usuario as any;
            if (u) {
              // Verificar campos específicos para determinar tipo
              if (u.telefone && !u.cargo && !u.salario && !u.tipoFuncionario) {
                tipo = 'CLIENTE';
              } else if (u.cargo || u.salario || u.tipoFuncionario) {
                tipo = 'FUNCIONARIO';
              } else {
                console.warn('TIPO DE USUÁRIO NÃO PÔDE SER DETERMINADO.');
              }

              console.warn('TIPO DE USUÁRIO INFERIDO COMO:', tipo);

            }
          }

          // Fallback: assumir CLIENTE se não conseguir determinar
          if (!tipo) {
            console.warn('Não foi possível determinar tipo de usuário; assumindo CLIENTE.');
            tipo = 'CLIENTE';
          }

          this.handleLoginSuccess(response, tipo);
        }),
        catchError(this.handleError) // Propaga o erro
      );
  }

  // --- MÉTODOS DE REGISTRO ---
  registerCliente(userData: RegisterClienteRequest): Observable<Usuario> {
    // ENDPOINT CORRETO: /api/clientes
    return this.apiService
      .post<Usuario>('/clientes', userData)
      .pipe(catchError(this.handleError));
  }

  registerFuncionario(userData: RegisterFuncionarioRequest): Observable<Usuario> {
    // ENDPOINT CORRETO: /api/funcionarios
    return this.apiService
      .post<Usuario>('/funcionarios', userData) // Ajustei para /api/funcionarios, verifique seu backend
      .pipe(catchError(this.handleError));
  }

  // --- MÉTODOS DE LOGIN ESPECÍFICOS (para compatibilidade) ---
  // Ambos usam o mesmo endpoint /auth/login
  loginCliente(credentials: { email: string; senha: string }): Observable<LoginResponse> {
    const loginRequest: LoginRequest = {
      ...credentials,
      // Adicione qualquer campo extra que seu backend precise para identificar tipo
      tipoUsuario: 'CLIENTE' // Se necessário
    };
    return this.login(loginRequest);
  }

  loginFuncionario(credentials: { email: string; senha: string }): Observable<LoginResponse> {
    const loginRequest: LoginRequest = {
      ...credentials,
      // Adicione qualquer campo extra que seu backend precise para identificar tipo
      tipoUsuario: 'FUNCIONARIO' // Se necessário
    };
    return this.login(loginRequest);
  }

  // --- LOGOUT ---
  logout(): void {
    // Remove tokens e dados do localStorage
    this.storage.removeItem('auth_token');
    this.storage.removeItem('user_data');
    this.storage.removeItem('user_type');
    this.storage.removeItem('isLoggedIn');

    // Reseta o estado nos signals
    this.currentUser.set(null);
    this.isAuthenticated.set(false);
    this.userType.set(null);
  }

  // Obter token JWT
  getToken(): string | null {
    return this.storage.getItem('auth_token');
  }

  // Verificar se token é válido
  private isTokenValid(token: string): boolean {
    try {
      const payload = this.decodeJWT(token);
      const now = Math.floor(Date.now() / 1000);
      return payload.exp > now;
    } catch (error) {
      return false;
    }
  }

  // Decodificar JWT
  private decodeJWT(token: string): any {
    try {
      const parts = token.split('.');
      if (parts.length !== 3) {
        throw new Error('Token JWT inválido');
      }
      
      const payload = parts[1];
      const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
      return JSON.parse(decoded);
    } catch (error) {
      throw new Error('Erro ao decodificar JWT');
    }
  }

  // --- VERIFICAR STATUS ---
  private checkAuthStatus(): void {
    const token = this.storage.getItem('auth_token');
    const userData = this.storage.getItem('user_data');
    const userType = this.storage.getItem('user_type') as 'CLIENTE' | 'FUNCIONARIO' | null;
    const isLoggedIn = this.storage.getItem('isLoggedIn') === 'true';

    if (token && isLoggedIn && userData && userType) {
      // Verificar se token ainda é válido
      if (this.isTokenValid(token)) {
        try {
          const user = JSON.parse(userData);
          this.currentUser.set(user);
          this.isAuthenticated.set(true);
          this.userType.set(userType);
        } catch (e) {
          console.error('Falha ao parsear dados do usuário:', e);
          this.logout();
        }
      } else {
        console.warn('Token JWT expirado');
        this.logout();
      }
    } else if (isLoggedIn && userData && userType) {
      // Caso não tenha token mas tenha dados (compatibilidade)
      try {
        const user = JSON.parse(userData);
        this.currentUser.set(user);
        this.isAuthenticated.set(true);
        this.userType.set(userType);
      } catch (e) {
        console.error('Falha ao parsear dados do usuário:', e);
        this.logout();
      }
    } else {
      this.logout();
    }
  }

  // --- TRATAR SUCESSO DO LOGIN ---
  private handleLoginSuccess(response: LoginResponse, type: 'CLIENTE' | 'FUNCIONARIO'): void {
    console.log('Processando login success:', { response, type });

    // Salvar token JWT se presente
    if ((response as any).token) {
      this.storage.setItem('auth_token', (response as any).token);
    }

  // Salvar dados do usuário (aceita 'dadosUsuario' vindo do backend ou 'usuario' por compatibilidade)
  const user = (response as any).dadosUsuario ?? (response as LoginResponse).usuario;
    this.storage.setItem('user_data', JSON.stringify(user));
    this.storage.setItem('user_type', type);
    this.storage.setItem('isLoggedIn', 'true'); // Marca como logado

    // Atualizar signals
    this.currentUser.set(user);
    this.isAuthenticated.set(true);
    this.userType.set(type);
  }

  // --- TRATAR ERROS ---
  private handleError(error: unknown): Observable<never> {
    console.error('Auth error:', error);
    return throwError(() => error);
  }

  // --- Observables (created during service construction / field initialization)
  // Creating these as fields ensures `effect()` inside signalToObservable runs
  // within an injection context (field initializers are run during DI construction).
  readonly currentUser$: Observable<Usuario | null> = signalToObservable(this.currentUser);
  readonly isAuthenticated$: Observable<boolean> = signalToObservable(this.isAuthenticated);
  readonly userType$: Observable<'CLIENTE' | 'FUNCIONARIO' | null> = signalToObservable(this.userType);

  // --- VERIFICAÇÕES DE TIPO ---
  isAdmin(): boolean {
    const user = this.currentUser();
    return (
      this.userType() === 'FUNCIONARIO' &&
      (user as any)?.cargo === 'ADMINISTRADOR'
    );
  }

  isFuncionario(): boolean {
    return this.userType() === 'FUNCIONARIO';
  }

  isCliente(): boolean {
    return this.userType() === 'CLIENTE';
  }
}
