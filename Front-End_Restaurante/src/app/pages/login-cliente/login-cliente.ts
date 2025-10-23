import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth/auth.service'; // Verifique o caminho

@Component({
  selector: 'app-login-cliente',
  standalone: true, // Adicione standalone: true se for um componente standalone
  imports: [FormsModule, CommonModule],
  templateUrl: './login-cliente.html',
  styleUrl: './login-cliente.css'
})
export class LoginCliente {
  // Propriedades do formulário
  nome = '';
  email = '';
  telefone = '';
  senha = '';
  endereco = '';

  // Estados da página
  isRegistrado = false;
  isLoading = false;
  erro = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit(): void {
    console.log('Form submitted with data:', {
      isRegistrado: this.isRegistrado,
      // Remova dados sensíveis como senha do log se for para produção
      email: this.email,
      // senha: this.senha
    });

    if (this.isValidForm()) {
      this.isLoading = true;
      this.erro = '';

      if (this.isRegistrado) {
        // --- Registro de Cliente ---
        const registerData = {
          nome: this.nome.trim(),
          email: this.email.trim(),
          senha: this.senha, // A senha será hasheada no backend
          telefone: this.telefone.trim(),
          endereco: this.endereco.trim() || undefined // Envia undefined se vazio
        };

        console.log('Sending registration data:', registerData);

        this.authService.registerCliente(registerData).subscribe({
          next: (response) => {
            console.log('Registration response:', response);
            // Idealmente, o backend retornaria uma mensagem clara de sucesso ou o usuário criado
            // Adaptando à resposta que você viu antes:
            if (response && response.email === registerData.email) {
               // Mudança: Ao invés de logar direto, apenas informa e muda para a tela de login
               this.erro = 'Conta criada com sucesso! Faça login agora.';
               this.isRegistrado = false; // Muda para a visão de login
               this.clearForm(); // Limpa o formulário
               this.isLoading = false;
              // this.loginAfterRegister(); // REMOVIDO - Melhor o usuário logar explicitamente
            } else {
              this.erro = response?.message || 'Erro ao criar conta. Verifique os dados.';
              this.isLoading = false;
            }
          },
          error: (error) => {
            console.error('Registration error:', error);
            // Tenta pegar a mensagem de erro específica do backend, se houver
            this.erro = error?.error?.message || error?.message || 'Erro ao criar conta. Verifique os dados e tente novamente.';
            this.isLoading = false;
          }
        });
      } else {
        // --- Login de Cliente ---
        const loginData = {
          email: this.email.trim(),
          senha: this.senha
        };

        console.log('Sending login data:', loginData);

        // 👇 USA A FUNÇÃO DE LOGIN UNIFICADA 👇
        this.authService.login(loginData).subscribe({
          next: (response) => {
            console.log('Login response:', response);
            // --- LÓGICA TEMPORÁRIA (PRÉ-JWT) ---
            // Verifica se o login foi marcado como sucesso no AuthService
            if (this.authService.isAuthenticated()) {
              // Verifica se o tipo de usuário logado é CLIENTE
              // ATENÇÃO: O backend SÓ AUTENTICA FUNCIONÁRIO AINDA!
              // Esta verificação falhará se o backend não retornar dados que permitam identificar o tipo,
              // ou se apenas funcionários podem logar via /api/auth/login.
              if (this.authService.isCliente()) {
                 console.log("Login de CLIENTE bem-sucedido (temporário). Navegando...");
                 this.router.navigate(['/']); // Navega para a página principal
              } else {
                 console.warn("Login bem-sucedido, mas usuário não é CLIENTE. Verifique o backend/lógica.");
                 this.erro = 'Login bem-sucedido, mas tipo de usuário inesperado.';
                 this.authService.logout(); // Desloga se o tipo não for cliente
              }

            } else {
              // Se isAuthenticated ainda for false, algo deu errado
              this.erro = response?.message || 'Falha no login. Verifique suas credenciais.';
            }
            this.isLoading = false;
          },
          error: (error) => {
            console.error('Login error:', error);
             // Tenta pegar a mensagem de erro específica (ex: "Credenciais inválidas" do AuthController)
            this.erro = error?.error || error?.message || 'Falha no login. Verifique suas credenciais.';
            this.isLoading = false;
          }
        });
      }
    } else {
       // Se o formulário não for válido, isValidForm já define a mensagem de erro
       console.log("Formulário inválido.");
    }
  }

  // REMOVIDO - O usuário fará login manualmente após o registro
  // private loginAfterRegister(): void { ... }


  // --- Funções de validação e UI (mantidas como estavam) ---

  private isValidForm(): boolean {
    // ... (código de validação mantido) ...
    console.log('Validating form...', { /* ... */ });
    if (!this.isRegistrado) { // LOGIN
      if (!this.email?.trim() || !this.senha) { // Senha não precisa de trim
        this.erro = 'Preencha email e senha'; return false;
      }
      if (!this.isValidEmail(this.email.trim())) {
        this.erro = 'Digite um e-mail válido'; return false;
      }
      // Removido validação de tamanho mínimo no frontend (backend deve validar)
      // if (this.senha.length < 6) { this.erro = 'Senha muito curta'; return false; }
      return true;
    } else { // CADASTRO
      if (!this.nome?.trim() || !this.email?.trim() || !this.telefone?.trim() || !this.senha) {
        this.erro = 'Preencha Nome, Email, Telefone e Senha'; return false;
      }
       if (!this.isValidEmail(this.email.trim())) {
        this.erro = 'Digite um e-mail válido'; return false;
      }
      // Removido validação de tamanho mínimo no frontend
      // if (this.senha.length < 6) { this.erro = 'Senha muito curta'; return false; }
      console.log('Form validation passed');
      return true;
    }
  }

  private isValidEmail(email: string): boolean {
    if (!email) return false;
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  toggleRegistro(): void {
    this.isRegistrado = !this.isRegistrado;
    this.erro = '';
    this.clearForm();
  }

  voltarSelecao(): void {
    this.router.navigate(['/seletor-login']);
  }

  recuperarSenha(): void {
    // Mantenha o alert ou implemente a funcionalidade
    alert('Funcionalidade em desenvolvimento');
  }

  private clearForm(): void {
    this.nome = '';
    this.email = '';
    this.telefone = '';
    this.senha = '';
    this.endereco = '';
  }
}