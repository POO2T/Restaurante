import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login-funcionario',
  imports: [FormsModule, CommonModule],
  templateUrl: './login-funcionario.html',
  styleUrl: './login-funcionario.css'
})
export class LoginFuncionario {
  protected email: string = '';
  protected senha: string = '';
  protected codigoFuncionario: string = '';
  protected erro: string = '';

  constructor(private router: Router) { }

  onSubmit() {
    // Reset erro
    this.erro = '';

    // Validações específicas para funcionários
    if (!this.email || !this.senha || !this.codigoFuncionario) {
      this.erro = 'Todos os campos são obrigatórios';
      return;
    }

    // Validação simples do código de funcionário (exemplo)
    if (this.codigoFuncionario.length < 4) {
      this.erro = 'Código do funcionário deve ter pelo menos 4 caracteres';
      return;
    }

    console.log("Login Funcionário:");
    console.log("Email:", this.email);
    console.log("Código:", this.codigoFuncionario);
    
    // Aqui você implementaria a autenticação real
    // Por enquanto, simula sucesso
    this.router.navigate(['/pedidos']); // Redireciona para área administrativa
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
