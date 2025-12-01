import { Comanda } from './comanda.model';
import { ItemPedido, ItemPedidoRequest } from './itemPedido.model';
import { statusPedido } from '../enums/statusPedido';

export interface Pedido {
  id: number;
  dataHora: Date;
  status: statusPedido;
  comanda: Comanda;
  itens: ItemPedido[];
  total: number;
}

export interface PedidoRequest {
  itens: ItemPedidoRequest[];
}
