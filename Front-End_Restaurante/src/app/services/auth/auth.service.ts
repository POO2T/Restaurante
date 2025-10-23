import { Injectable, signal } from '@angular/core';
import { Observable, BehaviorSubject, tap, catchError, throwError } from 'rxjs'; // Importe throwError
import { ApiService } from '../api'; // Seu serviço base para chamadas HTTP
import { LoginRequest, LoginResponse, RegisterClienteRequest, RegisterFuncionarioRequest } from '../../models/auth.model'; // Seus modelos
import { Usuario } from '../../models/user.model'; // Seu modelo base de usuário

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  // Mantém os Subjects para quem usa RxJS tradicionalmente
  private currentUserSubject = new BehaviorSubject<Usuario | null>(null);
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);

  // Signals para uma abordagem mais moderna e reativa no Angular
  currentUser = signal<Usuario | null>(null);
  isAuthenticated = signal<boolean>(false);
  userType = signal<'CLIENTE' | 'FUNCIONARIO' | null>(null);

  constructor(private apiService: ApiService) {
    // Tenta carregar o estado de autenticação ao iniciar o serviço
    this.checkAuthStatus();
  }

  // --- LOGIN UNIFICADO ---
  // Ambas as lógicas (cliente e funcionário) agora usam o mesmo endpoint de autenticação
  login(credentials: { email: string; senha: string }): Observable<any> { // Renomeado de loginCliente
    const loginEndpoint = '/api/auth/login'; // Endpoint correto no backend
    return this.apiService.post<any>(loginEndpoint, credentials) // Envia JSON
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
  registerCliente(userData: {
    nome: string; email: string; senha: string; telefone: string; endereco?: string;
  }): Observable<any> {
    // Endpoint CORRETO para cadastro de cliente
    return this.apiService.post('/api/clientes', userData)
      .pipe(catchError(this.handleError));
  }

  registerFuncionario(userData: RegisterFuncionarioRequest): Observable<any> {
    // Endpoint para cadastro de funcionário (VERIFICAR SE EXISTE NO BACKEND)
    // Se for o mesmo POST /api/funcionarios, ajuste aqui.
    return this.apiService.post('/api/funcionarios', userData) // Ajustei para /api/funcionarios, verifique seu backend
      .pipe(catchError(this.handleError));
  }

  // --- LOGOUT ---
  logout(): void {
    // Remove os itens temporários do localStorage
    localStorage.removeItem('user_data');
    localStorage.removeItem('user_type');
    localStorage.removeItem('isLoggedIn');
    // localStorage.removeItem('auth_token'); // Removerá o token JWT futuramente

    // Reseta o estado nos signals e subjects
    this.currentUser.set(null);
    this.isAuthenticated.set(false);
    this.userType.set(null);
    this.currentUserSubject.next(null);
    this.isAuthenticatedSubject.next(false);
  }

  // --- VERIFICAR STATUS (LÓGICA TEMPORÁRIA) ---
  private checkAuthStatus(): void {
    // ATENÇÃO: Esta lógica é TEMPORÁRIA e funciona sem token JWT.
    // PRECISARÁ SER REFEITA para validar um token JWT armazenado.
    const userData = localStorage.getItem('user_data');
    const userType = localStorage.getItem('user_type') as 'CLIENTE' | 'FUNCIONARIO' | null;
    const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true'; // Verifica a flag

    if (isLoggedIn && userData && userType) {
      try {
        const user = JSON.parse(userData);
        this.currentUser.set(user);
        this.isAuthenticated.set(true);
        this.userType.set(userType);
        this.currentUserSubject.next(user);
        this.isAuthenticatedSubject.next(true);
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
  private handleLoginSuccess(response: any, type: 'CLIENTE' | 'FUNCIONARIO'): void {
    // ATENÇÃO: Lógica TEMPORÁRIA! Salva a resposta direta do backend e uma flag.
    // SUBSTITUA quando o backend retornar um token JWT.
    console.warn("handleLoginSuccess chamado - Implementação JWT necessária!");

    // Salva a resposta do backend (que PODE conter dados do usuário, mas NÃO o token ainda)
    localStorage.setItem('user_data', JSON.stringify(response));
    localStorage.setItem('user_type', type);
    localStorage.setItem('isLoggedIn', 'true'); // Marca como logado

    // Atualiza o estado da aplicação
    this.currentUser.set(response);
    this.isAuthenticated.set(true);
    this.userType.set(type);
    this.currentUserSubject.next(response);
    this.isAuthenticatedSubject.next(true);
  }

  // --- TRATAR ERROS ---
  private handleError(error: any): Observable<never> {
    console.error('Auth error:', error);
    // Propaga o erro para o componente poder tratar (ex: mostrar mensagem na tela)
    return throwError(() => error);
  }

  // --- GETTERS (mantidos para compatibilidade) ---
  get currentUser$(): Observable<Usuario | null> {
    return this.currentUserSubject.asObservable();
  }

  get isAuthenticated$(): Observable<boolean> {
    return this.isAuthenticatedSubject.asObservable();
  }

  // --- VERIFICAÇÕES DE TIPO (mantidas) ---
  isAdmin(): boolean {
    const user = this.currentUser();
    // Ajuste a lógica conforme a estrutura real do seu objeto Funcionario na resposta
    return this.userType() === 'FUNCIONARIO' &&
           (user as any)?.cargo === 'GERENTE'; // Exemplo, verifique o nome exato do campo/enum
  }

  isFuncionario(): boolean {
    return this.userType() === 'FUNCIONARIO';
  }

  isCliente(): boolean {
    return this.userType() === 'CLIENTE';
  }
}
