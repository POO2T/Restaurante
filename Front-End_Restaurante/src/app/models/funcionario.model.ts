import { Usuario } from './user.model';
import { TipoFuncionario } from '../enums/tipoFuncionario';

export interface Funcionario extends Usuario {
  cargo: TipoFuncionario;
  salario: number;
  ativo: boolean;
  dataAdmissao: Date;
}