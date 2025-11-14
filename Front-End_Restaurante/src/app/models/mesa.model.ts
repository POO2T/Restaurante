import { statusMesa } from '../enums/statusMesa';

export interface Mesa {
    id: number;
    numero: number;
    nome: string;
    status: statusMesa;
}