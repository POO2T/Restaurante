import { Usuario } from './user.model'; 
import { Comanda } from './comanda.model';
import { PlanoFidelidade } from './planoFidelidade.model';

export interface Cliente extends Usuario {
  cpf: string;
  planoFidelidade?: PlanoFidelidade;
  historicoConsumo?: Comanda[];
}