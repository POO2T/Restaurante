import { Usuario } from './user.model';

export interface Funcionario extends Usuario {
  tipoFuncionario: TipoFuncionario;
  salario: number;
  dataAdmissao: Date;
}

export enum TipoFuncionario {
  GARCOM = 'GARCOM',
  COZINHEIRO = 'COZINHEIRO',
  GERENTE = 'GERENTE',
  CAIXA = 'CAIXA',
  ADMINISTRADOR = 'ADMINISTRADOR'
}