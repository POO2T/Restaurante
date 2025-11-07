export interface Usuario {
  id: number;
  nome: string;
  email?: string;
  senha: string;
  telefone: string;
  dataCriacao: Date;
}

export interface Cliente extends Usuario {
  cpf: string;
  planoFidelidadeId?: number;
}

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