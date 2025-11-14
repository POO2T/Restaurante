import { Routes } from '@angular/router';

// ROTAS PRINCIPAIS
import { Home } from './pages/home/home';
import { Cardapio } from './pages/cardapio/cardapio';
import { SeletorLogin } from './pages/seletor-login/seletor-login';

// ROTAS DE CLIENTES
import { LoginCliente } from './pages/login-cliente/login-cliente';

// ROTAS DE FUNCIONÁRIOS
import { LoginFuncionario } from './pages/login-funcionario/login-funcionario';
import { Mesas } from './pages/funcionarios/mesas/mesas';
import { Pedidos } from './pages/funcionarios/pedidos/pedidos';
import { Usuarios } from './pages/funcionarios/usuarios/usuarios';

import { AuthGuard } from './guards/auth.guard';
import { Produtos } from './pages/funcionarios/produtos/produtos';

export const routes: Routes = [
    { path: "", component: Home },
    { path: "cardapio", component: Cardapio },
    { path: "seletor-login", component: SeletorLogin },

    { path: "login-cliente", component: LoginCliente },

    { path: "login-funcionario", component: LoginFuncionario },
    { path: "funcionario/pedidos", component: Pedidos, canActivate: [AuthGuard], data: { role: 'FUNCIONARIO' } },
    { path: "funcionario/mesas", component: Mesas, canActivate: [AuthGuard], data: { role: 'FUNCIONARIO' } },
    { path: "funcionario/usuarios", component: Usuarios, canActivate: [AuthGuard], data: { role: 'FUNCIONARIO' } },
    { path: "funcionario/produtos", component: Produtos, canActivate: [AuthGuard], data: { role: 'FUNCIONARIO' } },

    { path: "**", redirectTo: "", pathMatch: "full" } // Rota coringa para páginas não encontradas
];
