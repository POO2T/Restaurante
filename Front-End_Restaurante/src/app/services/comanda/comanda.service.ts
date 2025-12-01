import { Injectable, inject } from '@angular/core';
import { catchError, Observable, tap, throwError } from 'rxjs';

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
    return this.apiService.get<Comanda>(`${this.endpoint}/${comandaId}/detalhes`).pipe(
      tap(c => console.debug('Detalhes da comanda recebidos:', c)),
      catchError((err) => {
        console.error('Erro ao buscar detalhes da comanda:', err);
        return throwError(() => err);
      })
    );
  }

  // Busca o histórico de comandas do cliente autenticado
  getHistoricoCliente(): Observable<Comanda[]> {
    return this.apiService.get<Comanda[]>(`${this.endpoint}/meu-historico`).pipe(
      tap(h => console.debug('Historico do cliente recebido:', h)),
      catchError((err) => {
        console.error('Erro ao buscar historico do cliente:', err);
        return throwError(() => err);
      })
    );
  }
}
