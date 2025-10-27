import { Injectable, signal, inject, effect } from '@angular/core';
import { Observable, tap, catchError, throwError } from 'rxjs'; // Importe throwError
import { signalToObservable } from '../../utils/signal-observable';
import { ApiService } from '../api'; // Seu serviço base para chamadas HTTP
import { StorageService } from '../storage.service';
import { LoginRequest, LoginResponse, RegisterClienteRequest, RegisterFuncionarioRequest } from '../../models/auth.model'; // Seus modelos
import { Usuario } from '../../models/user.model'; // Seu modelo base de usuário

@Injectable({
  providedIn: 'root'
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
  // Usa StorageService.isBrowser() para verificar se estamos no navegador
  // (StorageService já injeta PLATFORM_ID internamente)
  init() {
    if (this.storage.isBrowser()) {
      this.checkAuthStatus();
    }
  }

  // --- LOGIN UNIFICADO ---
  // Ambas as lógicas (cliente e funcionário) agora usam o mesmo endpoint de autenticação
  login(credentials: LoginRequest): Observable<LoginResponse> {
    const loginEndpoint = '/login'; // Endpoint correto no backend
    return this.apiService.post<LoginResponse>(loginEndpoint, credentials) // Envia JSON
      .pipe(
        tap(response => {
          // --- PONTO CRÍTICO PARA JWT ---
          // Quando o backend retornar um token, a lógica vai evoluir.
          console.log("Login success response:", response);

          if (!response) {
            throw new Error("Resposta de login inválida do servidor.");
          }

          // Preferência: o backend pode retornar explícito `tipoUsuario`.
          // Caso não retorne, usamos heurística com base nos campos do usuário.
          let tipo: 'CLIENTE' | 'FUNCIONARIO' = (response as any).tipoUsuario ?? null;
          if (!tipo) {
            const u = (response as LoginResponse).usuario as any;
            if (u) {
              if (u.cpf) tipo = 'CLIENTE';
              else if (u.tipoFuncionario || u.salario || u.dataAdmissao) tipo = 'FUNCIONARIO';
            }
          }

          // Fallback seguro: se não for possível determinar, assume FUNCIONARIO
          if (!tipo) {
            console.warn('Não foi possível determinar tipo de usuário no login; assumindo FUNCIONARIO por compatibilidade.');
            tipo = 'FUNCIONARIO';
          }

          this.handleLoginSuccess(response, tipo);
        }),
        catchError(this.handleError) // Propaga o erro
      );
  }

  // --- MÉTODOS DE REGISTRO ---
  registerCliente(userData: RegisterClienteRequest): Observable<Usuario> {
    // Endpoint CORRETO para cadastro de cliente
    return this.apiService.post<Usuario>('/clientes', userData)
      .pipe(catchError(this.handleError));
  }

  registerFuncionario(userData: RegisterFuncionarioRequest): Observable<Usuario> {
    // Endpoint para cadastro de funcionário (VERIFICAR SE EXISTE NO BACKEND)
    // Se for o mesmo POST /api/funcionarios, ajuste aqui.
    return this.apiService.post<Usuario>('/funcionarios', userData) // Ajustei para /api/funcionarios, verifique seu backend
      .pipe(catchError(this.handleError));
  }

  // --- LOGOUT ---
  logout(): void {
    // Remove os itens temporários do localStorage
    this.storage.removeItem('user_data');
    this.storage.removeItem('user_type');
    this.storage.removeItem('isLoggedIn');
    // localStorage.removeItem('auth_token'); // Removerá o token JWT futuramente

    // Reseta o estado nos signals e subjects
    this.currentUser.set(null);
    this.isAuthenticated.set(false);
    this.userType.set(null);
  }

  // --- VERIFICAR STATUS (LÓGICA TEMPORÁRIA) ---
  private checkAuthStatus(): void {
    // ATENÇÃO: Esta lógica é TEMPORÁRIA e funciona sem token JWT.
    // PRECISARÁ SER REFEITA para validar um token JWT armazenado.
    const userData = this.storage.getItem('user_data');
    const userType = (this.storage.getItem('user_type') as 'CLIENTE' | 'FUNCIONARIO' | null) || null;
    const isLoggedIn = this.storage.getItem('isLoggedIn') === 'true'; // Verifica a flag

        if (isLoggedIn && userData && userType) {
      try {
        const user = JSON.parse(userData);
        this.currentUser.set(user);
        this.isAuthenticated.set(true);
        this.userType.set(userType);
      } catch (e) {
        console.error("Falha ao parsear dados do usuário do localStorage", e);
        this.logout(); // Limpa dados inválidos se o parse falhar
      }
    } else {
      // Garante que o estado esteja limpo se não houver login salvo válido
      if (!isLoggedIn) { // Só faz logout se não estiver marcado como logado
         this.logout();
      }
    }
  }

  // --- TRATAR SUCESSO DO LOGIN (LÓGICA TEMPORÁRIA) ---
  private handleLoginSuccess(response: LoginResponse, type: 'CLIENTE' | 'FUNCIONARIO'): void {
    // ATENÇÃO: Lógica TEMPORÁRIA! Salva a resposta direta do backend e uma flag.
    // SUBSTITUA quando o backend retornar um token JWT.
    console.warn("handleLoginSuccess chamado - Implementação JWT necessária!");

    // Salva a resposta do backend (que inclui dados do usuário)
    const user = (response as LoginResponse).usuario;
    // Se o backend já retornou um token, armazene-o para uso futuro (JWT)
    if ((response as LoginResponse).token) {
      try {
        this.storage.setItem('auth_token', (response as LoginResponse).token);
      } catch (e) {
        console.warn('Não foi possível salvar token no storage:', e);
      }
    }
    this.storage.setItem('user_data', JSON.stringify(user));
    this.storage.setItem('user_type', type);
    this.storage.setItem('isLoggedIn', 'true'); // Marca como logado

    // Atualiza o estado da aplicação (signals)
    this.currentUser.set(user);
    this.isAuthenticated.set(true);
    this.userType.set(type);
  }

  // --- TRATAR ERROS ---
  private handleError(error: unknown): Observable<never> {
    console.error('Auth error:', error);
    // Propaga o erro original (ex: HttpErrorResponse) para o componente poder tratar
    // Dessa forma o componente pode acessar error.error.message vindo do backend
    return throwError(() => error);
  }

  // --- GETTERS (mantidos para compatibilidade) ---
  get currentUser$(): Observable<Usuario | null> {
    // Live Observable that emits whenever the signal changes.
    return signalToObservable(this.currentUser);
  }

  get isAuthenticated$(): Observable<boolean> {
    return signalToObservable(this.isAuthenticated);
  }

  // --- VERIFICAÇÕES DE TIPO (mantidas) ---
  isAdmin(): boolean {
    const user = this.currentUser();
    // Ajuste a lógica conforme a estrutura real do seu objeto Funcionario na resposta
    return this.userType() === 'FUNCIONARIO' &&
      ((user as unknown as { cargo?: string })?.cargo === 'GERENTE'); // Exemplo, verifique o nome exato do campo/enum
  }

  isFuncionario(): boolean {
    return this.userType() === 'FUNCIONARIO';
  }

  isCliente(): boolean {
    return this.userType() === 'CLIENTE';
  }
}
