import { Categoria } from './categoria.model';
import { statusProduto } from '../enums/statusProduto';

export interface Produto {
  id: number;
  nome: string;
  descricao: string;
  preco: number;
  quantidadeEstoque: number;
  disponibilidade: statusProduto;
  categoria: Categoria;
  imagemUrl: string;
}