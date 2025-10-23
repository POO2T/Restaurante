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
    const loginEndpoint = '/api/login'; // Endpoint correto no backend
    return this.apiService.post<LoginResponse>(loginEndpoint, credentials) // Envia JSON
      .pipe(
        tap(response => {
          // --- PONTO CRÍTICO PARA JWT ---
          // QUANDO O BACKEND RETORNAR UM TOKEN, ESTA LÓGICA VAI MUDAR COMPLETAMENTE
          console.log("Login success response:", response);

          // Lógica TEMPORÁRIA (pré-JWT): Determina o tipo baseado na resposta ou contexto
          // ATENÇÃO: O backend SÓ AUTENTICA FUNCIONÁRIO por enquanto!
          // Você precisará de uma forma de saber se é cliente ou funcionário
          // Talvez a resposta do backend inclua o tipo/role?
          // Se não, essa lógica aqui está INCORRETA para diferenciar.
          // Assumindo temporariamente que o login é sempre de Funcionário:
          if (response) { // Verifica se houve alguma resposta válida
             this.handleLoginSuccess(response, 'FUNCIONARIO'); // <<<<<<<< PRECISA SER AJUSTADO PARA CLIENTE
          } else {
             // Se a resposta for vazia ou inesperada, trate como erro
             throw new Error("Resposta de login inválida do servidor.");
          }

        }),
        catchError(this.handleError) // Propaga o erro
      );
  }

  // --- MÉTODOS DE REGISTRO ---
  registerCliente(userData: RegisterClienteRequest): Observable<Usuario> {
    // Endpoint CORRETO para cadastro de cliente
    return this.apiService.post<Usuario>('/api/clientes', userData)
      .pipe(catchError(this.handleError));
  }

  registerFuncionario(userData: RegisterFuncionarioRequest): Observable<Usuario> {
    // Endpoint para cadastro de funcionário (VERIFICAR SE EXISTE NO BACKEND)
    // Se for o mesmo POST /api/funcionarios, ajuste aqui.
    return this.apiService.post<Usuario>('/api/funcionarios', userData) // Ajustei para /api/funcionarios, verifique seu backend
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
    // Propaga o erro para o componente poder tratar (ex: mostrar mensagem na tela)
    const err = error instanceof Error ? error : new Error(typeof error === 'string' ? error : 'Erro desconhecido');
    return throwError(() => err);
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
