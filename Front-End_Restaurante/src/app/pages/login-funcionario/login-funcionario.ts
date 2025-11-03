import { Component, inject, ChangeDetectionStrategy } from '@angular/core';
import { Router } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth/auth.service';

@Component({
  selector: 'app-login-funcionario',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './login-funcionario.html',
  styleUrls: ['./login-funcionario.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginFuncionario {
  form: FormGroup;
  erro = '';

  private router = inject(Router);
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);

  private formatError(err: unknown, fallback = 'Ocorreu um erro'): string {
    if (!err) return fallback;
    if (typeof err === 'string') return err;
    if (err instanceof Error) return err.message || fallback;
    const anyErr = err as { error?: unknown; message?: string };
    if (anyErr.error)
      return typeof anyErr.error === 'string'
        ? anyErr.error
        : JSON.stringify(anyErr.error);
    if (anyErr.message) return anyErr.message;
    try {
      return JSON.stringify(err);
    } catch {
      return fallback;
    }
  }

  constructor() {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      senha: ['', Validators.required],
    });
  }

  onSubmit() {
    this.erro = '';

    console.log('Formulário enviado', { values: this.form.value });

    if (!this.form.valid) {
      this.erro = 'Formulário inválido. Preencha os campos obrigatórios.';
      return;
    }

    const v = this.form.value;
    const loginData = { email: (v.email || '').trim(), senha: v.senha };

    this.authService.login(loginData).subscribe({
      next: (response) => {
        // AuthService will set signals; check isAuthenticated
        if (this.authService.isAuthenticated()) {
          if (this.authService.isFuncionario()) {
            console.log('Login Funcionário bem-sucedido. Redirecionando para /pedidos');
            this.router.navigate(['/pedidos']);
          } else {
            this.erro = 'Login bem-sucedido, mas tipo de usuário inesperado.';
            this.authService.logout();
            this.router.navigate(['/login-cliente']);
          }
        } else {
          this.erro = 'Falha no login. Verifique suas credenciais.';
        }
      },
      error: (error) => {
        this.erro = this.formatError(
          error,
          'Falha no login. Verifique suas credenciais.'
        );
      },
    });

    console.log('Login Funcionário:', {
      email: v.email,
      codigo: v.codigoFuncionario,
    });
    //this.router.navigate(['/pedidos']);
  }

  voltarSelecao() {
    this.router.navigate(['/']);
  }

  recuperarSenha() {
    alert(
      'Funcionalidade de recuperação de senha será implementada em breve.\nEntre em contato com o administrador do sistema.'
    );
  }

  suporteTecnico() {
    alert(
      'Suporte Técnico:\nTelefone: (11) 99999-9999\nEmail: suporte@bellapiatto.com\nHorário: Segunda a Sexta, 8h às 18h'
    );
  }
}
