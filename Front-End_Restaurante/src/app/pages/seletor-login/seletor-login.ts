import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-seletor-login',
  imports: [CommonModule],
  templateUrl: './seletor-login.html',
  styleUrl: './seletor-login.css'
})
export class SeletorLogin {

  constructor(private router: Router) { }

  navegarParaFuncionario() {
    this.router.navigate(['/login-funcionario']);
  }

  navegarParaCliente() {
    this.router.navigate(['/login-cliente']);
  }
}
