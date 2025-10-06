import { Component } from '@angular/core';

@Component({
  selector: 'app-login',
  imports: [],
  templateUrl: './login.html',
  styleUrl: './login.css'
})

export class Login {

  constructor() { }

  onSubmit() {
    // LÓGICA DE AUTENTICAÇÃO AQUI
    // NO FUTURO: IMPLEMENTAR AUTENTICAÇÃO REAL
    console.log('Formulário enviado');
  }

}
