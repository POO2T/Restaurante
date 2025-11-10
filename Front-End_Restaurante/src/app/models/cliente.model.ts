import { Usuario } from './user.model';

export interface Cliente extends Usuario {
  cpf: string;
  planoFidelidadeId?: number;
}