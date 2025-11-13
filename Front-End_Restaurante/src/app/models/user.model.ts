export interface Usuario {
  id: number;
  nome: string;
  email: string;
  senha: string;
  telefone?: string;
  dataCriacao: Date;
}