import { Component, inject, ChangeDetectionStrategy } from '@angular/core';
import { Router } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';

import { AuthService } from '../../services/auth/auth.service';
import { formatError } from '../../utils/formatError';

@Component({
  selector: 'app-login-funcionario',
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

    this.authService.loginFuncionario(loginData).subscribe({
      next: (response) => {
        // AuthService will set signals; check isAuthenticated
        console.log('Após login (funcionario) - userType signal:', this.authService.userType());
        if (this.authService.isAuthenticated()) {
          if (this.authService.isFuncionario()) {
            console.log('Login Funcionário bem-sucedido. Redirecionando para /funcionarios/pedidos');
            this.router.navigate(['/funcionario/pedidos']);
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
        this.erro = formatError(error, 'Falha no login. Verifique suas credenciais.');
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
