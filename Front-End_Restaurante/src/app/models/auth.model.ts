import { Usuario } from "./user.model";
import { TipoFuncionario } from "./user.model";

export interface LoginRequest {
  telefone: string;
  senha: string;
}

export interface LoginResponse {
  token: string;
  usuario: Usuario;
  tipoUsuario: 'CLIENTE' | 'FUNCIONARIO';
  expiresIn: number;
}

export interface RegisterClienteRequest {
  nome: string;
  email?: string;
  senha: string;
  cpf: string;
  telefone: string;
}

export interface RegisterFuncionarioRequest {
  nome: string;
  email: string;
  senha: string;
  tipoFuncionario: TipoFuncionario;
  salario: number;
  telefone?: string;
}