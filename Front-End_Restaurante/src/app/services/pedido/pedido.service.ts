import { Injectable, inject } from '@angular/core';
import { catchError, Observable, tap, throwError } from 'rxjs';

import { Pedido, PedidoRequest } from '../../models/pedido.model';
import { ItemPedido } from '../../models/itemPedido.model';
import { statusPedido } from '../../enums/statusPedido';
import { ApiService } from '../api';
import { Comanda } from '../../models/comanda.model';

@Injectable({
  providedIn: 'root',
})
export class PedidoService {
  private readonly endpoint = '/pedidos';
  private apiService = inject(ApiService);

  postPedido(
    comanda: Comanda,
    pedidoRequest: PedidoRequest
  ): Observable<Pedido> {
    return this.apiService
      .post<Pedido, PedidoRequest>(
        `/comandas/${comanda.id}${this.endpoint}`,
        pedidoRequest
      )
      .pipe(
        tap((pedido) => {
          console.log('Pedido criado com sucesso:', pedido);
        }),
        catchError((error) => {
          console.error('Erro ao criar pedido:', error);
          return throwError(() => error);
        })
      );
  }
}
