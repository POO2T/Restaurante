import { Injectable, signal, inject, effect } from '@angular/core';
import { Observable, tap, catchError, throwError } from 'rxjs'; // Importe throwError
import { signalToObservable } from '../../utils/signal-observable';
import { ApiService } from '../api'; // Seu serviço base para chamadas HTTP
import { Mesa } from '../../models/mesa.model';

@Injectable({
  providedIn: 'root',
})
export class MesasService {
  private apiService = inject(ApiService);

  getMesas(): Observable<Mesa[]> {
    return this.apiService.get<Mesa[]>('/mesas').pipe(
      tap((mesas) => {
        console.log('Mesas fetched:', mesas);
      }),
      catchError((error) => {
        console.error('Error fetching mesas:', error);
        return throwError(() => new Error('Failed to fetch mesas'));
      })
    );
  }

  postMesa(mesa: Partial<Mesa>): Observable<Mesa> {
    return this.apiService.post<Mesa>('/mesas', mesa).pipe(
      tap((newMesa) => {
        console.log('Mesa criada:', newMesa);
      }),
      catchError((error) => {
        console.error('Error creating mesa:', error);
        return throwError(() => new Error('Failed to create mesa'));
      })
    );
  }
}
