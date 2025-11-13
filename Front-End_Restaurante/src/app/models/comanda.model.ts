import { Cliente } from './cliente.model';
import { Pedido } from './pedido.model';
import { Mesa } from './mesa.model';

import { statusComanda } from '../enums/statusComanda';

export interface Comanda {
  id: number;
  dataAbertura: Date | null;
  dataFechamento: Date | null;
  status: statusComanda;
  cliente: Cliente | null;
  mesa: Mesa | null;
  pedidos: Pedido[];
}
