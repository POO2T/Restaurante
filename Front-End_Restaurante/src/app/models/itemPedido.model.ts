import { Pedido } from './pedido.model';
import { Produto } from './produto.model';

export interface ItemPedido {
  id: number;
  quantidade: number;
  precoUnitario: number;
  pedido: Pedido;
  produto: Produto;
}

export interface ItemPedidoRequest {
  produtoId: number;
  quantidade: number;
}
