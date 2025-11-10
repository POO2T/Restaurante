export interface Mesa {
    id: number;
    numero: number;
    nome: string;
    status: statusMesa;
}

export enum statusMesa {
    OCUPADA = 'OCUPADA',
    DISPONIVEL = 'DISPONIVEL'
}