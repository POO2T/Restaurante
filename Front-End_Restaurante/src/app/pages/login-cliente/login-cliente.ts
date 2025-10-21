import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth/auth.service';

@Component({
  selector: 'app-login-cliente',
  imports: [FormsModule, CommonModule],
  templateUrl: './login-cliente.html',
  styleUrl: './login-cliente.css'
})
export class LoginCliente {
  // Propriedades do formulário
  email = '';
  telefone = '';
  senha = '';
  
  // Estados da página
  isRegistrado = false;
  isLoading = false;
  erro = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit(): void {
    if (this.isValidForm()) {
      this.isLoading = true;
      this.erro = '';

      if (this.isRegistrado) {
        // Registro de cliente
        const registerData = {
          nome: this.email.split('@')[0], // Nome temporário baseado no email
          email: this.email,
          senha: this.senha,
          telefone: this.telefone,
          cpf: '' // Será necessário adicionar campo CPF depois
        };

        this.authService.registerCliente(registerData).subscribe({
          next: () => {
            // Após registrar, fazer login automático
            this.loginAfterRegister();
          },
          error: (error) => {
            this.erro = 'Erro ao criar conta. Tente novamente.';
            this.isLoading = false;
          }
        });
      } else {
        // Login de cliente (só telefone e senha)
        const loginData = {
          telefone: this.telefone,
          senha: this.senha
        };

        this.authService.loginCliente(loginData).subscribe({
          next: () => {
            this.router.navigate(['/']);
          },
          error: (error) => {
            this.erro = 'Credenciais inválidas';
            this.isLoading = false;
          },
          complete: () => {
            this.isLoading = false;
          }
        });
      }
    }
  }

  private loginAfterRegister(): void {
    const loginData = {
      telefone: this.telefone,
      senha: this.senha
    };

    this.authService.loginCliente(loginData).subscribe({
      next: () => {
        this.router.navigate(['/']);
      },
      error: (error) => {
        this.erro = 'Conta criada! Faça login agora.';
        this.isRegistrado = false;
        this.isLoading = false;
      },
      complete: () => {
        this.isLoading = false;
      }
    });
  }

  private isValidForm(): boolean {
    // Validações básicas sempre necessárias
    if (!this.telefone || !this.senha) {
      this.erro = 'Preencha todos os campos obrigatórios';
      return false;
    }

    if (this.senha.length < 6) {
      this.erro = 'A senha deve ter pelo menos 6 caracteres';
      return false;
    }

    // Validações específicas para REGISTRO
    if (this.isRegistrado) {
      if (!this.email) {
        this.erro = 'E-mail é obrigatório para cadastro';
        return false;
      }
      
      if (!this.isValidEmail(this.email)) {
        this.erro = 'Digite um e-mail válido';
        return false;
      }
    }

    // Para LOGIN: só valida telefone e senha (já validados acima)
    return true;
  }

  private isValidEmail(email: string): boolean {
    if (!email) return false; // Se email estiver vazio
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
    // Implementar recuperação de senha
    alert('Funcionalidade em desenvolvimento');
  }

  private clearForm(): void {
    this.email = '';
    this.telefone = '';
    this.senha = '';
  }
}
