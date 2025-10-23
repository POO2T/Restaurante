import { Component, inject, ChangeDetectionStrategy } from '@angular/core';
import { Router } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login-funcionario',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './login-funcionario.html',
  styleUrls: ['./login-funcionario.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LoginFuncionario {
  form: FormGroup;
  erro = '';

  private router = inject(Router);
  private fb = inject(FormBuilder);

  constructor() {
    this.form = this.fb.group({
      codigoFuncionario: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      senha: ['', Validators.required]
    });
  }

  onSubmit() {
    this.erro = '';
    if (!this.form.valid) {
      this.erro = 'Todos os campos são obrigatórios e devem ser válidos.';
      return;
    }

    const v = this.form.value;
    if (v.codigoFuncionario.length < 4) {
      this.erro = 'Código do funcionário deve ter pelo menos 4 caracteres';
      return;
    }

    console.log('Login Funcionário:', { email: v.email, codigo: v.codigoFuncionario });
    this.router.navigate(['/pedidos']);
  }

  voltarSelecao() {
    this.router.navigate(['/']);
  }

  recuperarSenha() {
    alert('Funcionalidade de recuperação de senha será implementada em breve.\nEntre em contato com o administrador do sistema.');
  }

  suporteTecnico() {
    alert('Suporte Técnico:\nTelefone: (11) 99999-9999\nEmail: suporte@bellapiatto.com\nHorário: Segunda a Sexta, 8h às 18h');
  }
}
