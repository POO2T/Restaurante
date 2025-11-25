import { inject, Injectable } from '@angular/core';

import { ApiService } from '../api';
import { Produto, ProdutoRequest } from '../../models/produto.model';
import { Observable } from 'rxjs/internal/Observable';
import { catchError, tap, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProdutoService {
  
  private readonly endpoint = '/produtos';
  private apiService: ApiService = inject(ApiService);

  constructor() { }


  getProdutos(): Observable<Produto[]> {
    return this.apiService.get<Produto[]>(this.endpoint).pipe(
      tap((produtos) => {
        console.log('Produtos:', produtos);
      }),
      catchError((error) => {
        console.error('Error fetching produtos:', error);
        return throwError(() => new Error('Failed to fetch produtos'));
      })
    );
  }

  postProduto(produto: Partial<ProdutoRequest>): Observable<Produto> {
    return this.apiService.post<Produto>(this.endpoint, produto).pipe(
      tap((newProduto) => {
        console.log('Produto criado:', newProduto);
      }),
      catchError((error) => {
        console.error('Error creating produto:', error);
        return throwError(() => new Error('Failed to create produto'));
      })
    );
  }

  putProduto(id: number, produto: Partial<ProdutoRequest>): Observable<Produto> {
    return this.apiService.put<Produto>(`${this.endpoint}/${id}`, produto).pipe(
        tap((updatedProduto) => {
          console.log('Produto atualizado:', updatedProduto);
        }),
        catchError((error) => {
          console.error('Error updating produto:', error);
          return throwError(() => new Error('Failed to update produto'));
        })
      );
  }

  deleteProduto(produto: Partial<Produto>): Observable<void> {
    return this.apiService.delete<void>(`${this.endpoint}/${produto.id}`).pipe(
      tap(() => {
        console.log('Produto deletado:', produto.id);
      }), 
      catchError((error) => {
        console.error('Error deleting produto:', error);
        return throwError(() => new Error('Failed to delete produto'));
      })
    );
  }
}