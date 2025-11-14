import { Produto } from './produto.model';

export interface Categoria {
  id: number;
  nome: string;
  produtos: Produto[];
}