import { Comanda } from './comanda.model';
import { ItemPedido } from './itemPedido.model';
import { statusPedido } from '../enums/statusPedido';

export interface Pedido {
  id: number;
  dataHora: Date;
  status: statusPedido;
  comanda: Comanda;
  itens: ItemPedido[];
}