import { Routes } from '@angular/router';
import { LoginFuncionario } from './pages/login-funcionario/login-funcionario';
import { LoginCliente } from './pages/login-cliente/login-cliente';
import { SeletorLogin } from './pages/seletor-login/seletor-login';
import { Cardapio } from './pages/cardapio/cardapio';
import { Pedidos } from './pages/pedidos/pedidos';
import { Home } from './pages/home/home';

export const routes: Routes = [
    { path: "", component: Home },
    { path: "seletor-login", component: SeletorLogin },
    { path: "login-funcionario", component: LoginFuncionario },
    { path: "login-cliente", component: LoginCliente },
    { path: "cardapio", component: Cardapio },
    { path: "pedidos", component: Pedidos },
    { path: "**", redirectTo: "", pathMatch: "full" } // Rota coringa para páginas não encontradas
];
