import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { Cardapio } from './pages/cardapio/cardapio';
import { Pedidos } from './pages/pedidos/pedidos';

export const routes: Routes = [
    { path: "", redirectTo: "/login", pathMatch: "full" },
    { path: "login", component: Login },
    { path: "cardapio", component: Cardapio },
    { path: "pedidos", component: Pedidos }
];
