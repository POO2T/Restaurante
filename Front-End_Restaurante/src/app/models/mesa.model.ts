export interface Mesa {
    id: number;
    numero: number;
    nome: string;
    status: 'OCUPADA' | 'DISPONIVEL' | 'RESERVADA';
}