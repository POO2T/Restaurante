import { Component, inject, ChangeDetectionStrategy } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-seletor-login',
  imports: [CommonModule],
  templateUrl: './seletor-login.html',
  styleUrls: ['./seletor-login.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SeletorLogin {
  private router = inject(Router);

  navegarParaFuncionario() {
    this.router.navigate(['/login-funcionario']);
  }

  navegarParaCliente() {
    this.router.navigate(['/login-cliente']);
  }
}
