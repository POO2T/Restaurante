import { Comanda } from './comanda.model';
import { FormaPagamento } from '../enums/formaPagamento';

export interface Pagamento {
    id: number;
    dataPagamento: Date;
    valor: number;
    forma: FormaPagamento;
    comanda: Comanda;
}

