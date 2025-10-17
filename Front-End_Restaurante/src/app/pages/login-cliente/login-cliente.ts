import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login-cliente',
  imports: [FormsModule, CommonModule],
  templateUrl: './login-cliente.html',
  styleUrl: './login-cliente.css'
})
export class LoginCliente {
  protected email: string = '';
  protected senha: string = '';
  protected telefone: string = '';
  protected erro: string = '';
  protected isRegistrado: boolean = false;

  constructor(private router: Router) { }

  onSubmit() {
    // Reset erro
    this.erro = '';

    // Validações básicas
    if (!this.email || !this.senha) {
      this.erro = 'Email e senha são obrigatórios';
      return;
    }

    if (this.isRegistrado && !this.telefone) {
      this.erro = 'Telefone é obrigatório para cadastro';
      return;
    }

    if (this.isRegistrado) {
      console.log("Cadastro Cliente:");
      console.log("Email:", this.email);
      console.log("Telefone:", this.telefone);
      // Aqui implementaria o cadastro
      alert('Cadastro realizado com sucesso!');
      this.isRegistrado = false;
    } else {
      console.log("Login Cliente:");
      console.log("Email:", this.email);
      // Aqui implementaria a autenticação
      this.router.navigate(['/cardapio']); // Redireciona para área do cliente
    }
  }

  toggleRegistro() {
    this.isRegistrado = !this.isRegistrado;
    this.erro = '';
    this.limparCampos();
  }

  limparCampos() {
    this.email = '';
    this.senha = '';
    this.telefone = '';
  }

  voltarSelecao() {
    this.router.navigate(['/']);
  }

  recuperarSenha() {
    const email = prompt('Digite seu email para recuperação de senha:');
    if (email) {
      alert(`Link de recuperação enviado para: ${email}\nVerifique sua caixa de entrada e spam.`);
    }
  }
}
