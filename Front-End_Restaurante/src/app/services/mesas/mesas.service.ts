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
  private readonly endpoint = '/mesas';

  getMesas(): Observable<Mesa[]> {
    return this.apiService.get<Mesa[]>(this.endpoint).pipe(
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
    return this.apiService.post<Mesa>(this.endpoint, mesa).pipe(
      tap((newMesa) => {
        console.log('Mesa criada:', newMesa);
      }),
      catchError((error) => {
        console.error('Error creating mesa:', error);
        return throwError(() => new Error('Failed to create mesa'));
      })
    );
  }

  putMesa(id: number, mesa: Partial<Mesa>): Observable<Mesa> {
    return this.apiService.put<Mesa>(`/mesas/${id}`, mesa).pipe(
      tap((updatedMesa) => {
        console.log('Mesa atualizada:', updatedMesa);
      }),
      catchError((error) => {
        console.error('Error updating mesa:', error);
        return throwError(() => new Error('Failed to update mesa'));
      })
    );
  }

  // Atualiza apenas o status da mesa (usa endpoint /mesas/{id}/status)
  putMesaStatus(id: number, status: string): Observable<Mesa> {
    return this.apiService.put<Mesa>(`${this.endpoint}/${id}/status`, { status }).pipe(
      tap((updatedMesa) => {
        console.log('Status da mesa atualizado:', updatedMesa);
      }),
      catchError((error) => {
        console.error('Error updating mesa status:', error);
        return throwError(() => new Error('Failed to update mesa status'));
      })
    );
  }

  deleteMesa(id: number): Observable<Mesa> {
    return this.apiService.delete<Mesa>(`${this.endpoint}/${id}`).pipe(
      tap((deletedMesa) => {
        console.warn('Mesa deletada:', deletedMesa);
      }),
      catchError((error) => {
        console.error('Error deleting mesa:', error);
        return throwError(() => new Error('Failed to delete mesa'));
      })
    );
  }

}
