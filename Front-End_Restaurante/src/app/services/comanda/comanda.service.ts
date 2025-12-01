import { Injectable, inject } from '@angular/core';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { map } from 'rxjs/operators';

import { Comanda, ComandaAberturaRequest } from '../../models/comanda.model';
import { ApiService } from '../api';
import { AuthService } from '../auth/auth.service';
import { Cliente } from '../../models/cliente.model';

@Injectable({
  providedIn: 'root',
})
export class ComandaService {
  private readonly endpoint = '/comandas';
  private apiService = inject(ApiService);
  private authService = inject(AuthService);

  // Abre uma comanda para um cliente autenticado
  postComandaAutenticada(
    comandaRequest: ComandaAberturaRequest
  ): Observable<Comanda> {
    const clienteEmail =
      this.authService.currentUser() &&
      (this.authService.currentUser() as Cliente).email;
    if (!clienteEmail) {
      throw new Error('Usuário não autenticado');
    }
    return this.apiService
      .post<Comanda, ComandaAberturaRequest>(
        `${this.endpoint}/autenticada`,
        comandaRequest
      )
      .pipe(
        tap((comanda) => {
          console.log(
            'Comanda aberta com sucesso para cliente autenticado:',
            comanda
          );
        }),
        catchError((error) => {
          console.error(
            'Erro ao abrir comanda para cliente autenticado:',
            error
          );
          return throwError(() => error);
        })
      );
  }

  // Realiza o pagamento e fecha a comanda
  pagarComanda(
    comandaId: number,
    pagamento: { formaPagamento: string; valorPago: number }
  ): Observable<Comanda> {
    return this.apiService
      .post<any, any>(`${this.endpoint}/${comandaId}/pagar`, pagamento)
      .pipe(
        map((dto) => this.mapDetalhadaDtoToComanda(dto)),
        tap((c) => console.debug('Comanda fechada e mapeada:', c)),
        catchError((err) => {
          console.error('Erro ao pagar/fechar comanda:', err);
          return throwError(() => err);
        })
      );
  }

  postComandaVisitante(
    comandaRequest: ComandaAberturaRequest
  ): Observable<Comanda> {
    return this.apiService
      .post<Comanda>(`${this.endpoint}/visitante`, comandaRequest)
      .pipe(
        tap((comanda) => {
          console.log('Comanda aberta com sucesso para visitante:', comanda);
        }),
        catchError((error) => {
          console.error('Erro ao abrir comanda para visitante:', error);
          return throwError(() => error);
        })
      );
  }

  // Busca os detalhes completos de uma comanda (inclui pedidos e itens)
  getDetalhesComanda(comandaId: number): Observable<Comanda> {
    return this.apiService
      .get<any>(`${this.endpoint}/${comandaId}/detalhes`)
      .pipe(
        map((dto) => this.mapDetalhadaDtoToComanda(dto)),
        tap((c) =>
          console.debug('Detalhes da comanda (mapeados) recebidos:', c)
        ),
        catchError((err) => {
          console.error('Erro ao buscar detalhes da comanda:', err);
          return throwError(() => err);
        })
      );
  }

  // Busca o histórico de comandas do cliente autenticado
  getHistoricoCliente(): Observable<Comanda[]> {
    return this.apiService.get<any[]>(`${this.endpoint}/meu-historico`).pipe(
      map((arr) =>
        (arr || []).map((dto) => this.mapDetalhadaDtoToComanda(dto))
      ),
      tap((h) => console.debug('Historico do cliente (mapeado) recebido:', h)),
      catchError((err) => {
        console.error('Erro ao buscar historico do cliente:', err);
        return throwError(() => err);
      })
    );
  }

  // Converte o DTO detalhado do backend para a estrutura de frontend `Comanda`
  private mapDetalhadaDtoToComanda(dto: any): Comanda {
    if (!dto) return null as any;

    const pedidos = (dto.pedidos || []).map((p: any) => {
      const itens = (p.itens || []).map((it: any) => {
        const produto = {
          id: it.idProd != null ? it.idProd : null,
          nome: it.nomeProduto || '',
          descricao: it.descricao || '',
          preco: it.precoUnitario || it.preco || 0,
          quantidadeEstoque:
            it.quantidadeEstoque != null ? it.quantidadeEstoque : 0,
          disponibilidade:
            it.disponibilidade != null ? it.disponibilidade : true,
          categoria: it.categoria != null ? it.categoria : null,
        } as any;

        return {
          id: it.id ?? null,
          quantidade: it.quantidade ?? 0,
          precoUnitario: it.precoUnitario ?? 0,
          pedido: null,
          produto,
        } as any;
      });

      return {
        id: p.id ?? null,
        dataHora: p.dataHora ? new Date(p.dataHora) : null,
        status: p.status,
        comanda: { id: dto.id } as any,
        itens,
        total: p.totalPedido ?? p.total ?? 0,
      } as any;
    });

    const cliente = dto.nomeCliente
      ? ({
          id: null,
          nome: dto.nomeCliente,
          email: null,
          senha: null,
          dataCriacao: null,
        } as any)
      : null;

    const mesa =
      dto.numeroMesa != null || dto.nomeMesa != null
        ? ({
            id: null,
            numero: dto.numeroMesa ?? null,
            nome: dto.nomeMesa ?? null,
            status: null,
          } as any)
        : null;

    return {
      id: dto.id,
      dataAbertura: dto.dataAbertura ? new Date(dto.dataAbertura) : null,
      dataFechamento: dto.dataFechamento ? new Date(dto.dataFechamento) : null,
      status: dto.status,
      cliente,
      mesa,
      pedidos,
      total:
        dto.totalCalculado ??
        dto.total ??
        pedidos.reduce((s: number, p: any) => s + (p.total || 0), 0),
    } as Comanda;
  }
}
