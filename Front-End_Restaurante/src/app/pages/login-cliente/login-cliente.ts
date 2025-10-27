import { Component, inject, ChangeDetectionStrategy } from '@angular/core';
import { Router } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth/auth.service';

@Component({
  selector: 'app-login-cliente',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './login-cliente.html',
  styleUrls: ['./login-cliente.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LoginCliente {
  form: FormGroup;

  isRegistrado = false;
  isLoading = false;
  erro = '';

  private authService = inject(AuthService);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  private formatError(err: unknown, fallback = 'Ocorreu um erro'): string {
    if (!err) return fallback;
    if (typeof err === 'string') return err;
    if (err instanceof Error) return err.message || fallback;
    const anyErr = err as { error?: unknown; message?: string };
    if (anyErr.error) return typeof anyErr.error === 'string' ? anyErr.error : JSON.stringify(anyErr.error);
    if (anyErr.message) return anyErr.message;
    try { return JSON.stringify(err); } catch { return fallback; }
  }

  

  constructor() {
    this.form = this.fb.group({
      nome: [''],
      email: ['', [Validators.required, Validators.email]],
      telefone: [''],
      senha: ['', Validators.required],
      endereco: ['']
    });
  }

  onSubmit(): void {
    console.log('Form submitted', { isRegistrado: this.isRegistrado, values: this.form.value });

    if (!this.form.valid) {
      this.erro = 'Formulário inválido. Preencha os campos obrigatórios.';
      return;
    }

    this.isLoading = true;
    this.erro = '';

    if (this.isRegistrado) {
      const v = this.form.value;
      const registerData = {
        nome: (v.nome || '').trim(),
        email: (v.email || '').trim(),
        senha: v.senha,
        cpf: (v['cpf'] || '').trim() || undefined,
        telefone: (v.telefone || '').trim()
      };

      this.authService.registerCliente(registerData).subscribe({
        next: (usuario) => {
          if (usuario && usuario.email === registerData.email) {
            this.erro = 'Conta criada com sucesso! Faça login agora.';
            this.isRegistrado = false;
            this.clearForm();
          } else {
            this.erro = 'Erro ao criar conta. Verifique os dados.';
          }
          this.isLoading = false;
        },
        error: (error) => {
          this.erro = this.formatError(error, 'Erro ao criar conta. Verifique os dados e tente novamente.');
          this.isLoading = false;
        }
      });
    } else {
      const v = this.form.value;
      const loginData = { email: (v.email || '').trim(), senha: v.senha };

      this.authService.login(loginData).subscribe({
        next: (response) => {
          // AuthService will set signals; check isAuthenticated
          if (this.authService.isAuthenticated()) {
            if (this.authService.isCliente()) {
              this.router.navigate(['/']);
            } else {
              this.erro = 'Login bem-sucedido, mas tipo de usuário inesperado.';
              this.authService.logout();
            }
          } else {
            this.erro = 'Falha no login. Verifique suas credenciais.';
          }
          this.isLoading = false;
        },
        error: (error) => {
          this.erro = this.formatError(error, 'Falha no login. Verifique suas credenciais.');
          this.isLoading = false;
        }
      });
    }
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
    alert('Funcionalidade em desenvolvimento');
  }

  private clearForm(): void {
    this.form.reset();
  }
}