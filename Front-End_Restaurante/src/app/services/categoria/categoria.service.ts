import { inject, Injectable } from '@angular/core';

import { ApiService } from '../api';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { Categoria } from '../../models/categoria.model';

@Injectable({
  providedIn: 'root',
})
export class CategoriaService {
  private readonly endpoint = '/categorias';
  private apiService = inject(ApiService);

  constructor() {}

  getCategorias(): Observable<Categoria[]> {
    return this.apiService.get<Categoria[]>(this.endpoint).pipe(
      tap((categorias) => {
        console.log('Categorias:', categorias);
      }),
      catchError((error) => {
        console.error('Error fetching categorias:', error);
        return throwError(() => new Error('Failed to fetch categorias'));
      })
    );
  }

  postCategoria(categoria: Partial<Categoria>): Observable<Categoria> {
    return this.apiService.post<Categoria>(this.endpoint, categoria).pipe(
      tap((newCategoria) => {
        console.log('Categoria criada:', newCategoria);
      }),
      catchError((error) => {
        console.error('Error creating categoria:', error);
        return throwError(() => new Error('Failed to create categoria'));
      })
    );
  }

  putCategoria(categoria: Partial<Categoria>): Observable<Categoria> {
    return this.apiService
      .put<Categoria>(`${this.endpoint}/${categoria.id}`, categoria)
      .pipe(
        tap((updatedCategoria) => {
          console.log('Categoria atualizada:', updatedCategoria);
        }),
        catchError((error) => {
          console.error('Error updating categoria:', error);
          return throwError(() => new Error('Failed to update categoria'));
        })
      );
  }

  deleteCategoria(categoria: Partial<Categoria>): Observable<Categoria> {
    return this.apiService
      .delete<Categoria>(`${this.endpoint}/${categoria.id}`)
      .pipe(
        tap(() => {
          console.log('Categoria deletada:', categoria.id);
        }),
        catchError((error) => {
          console.error('Error deleting categoria:', error);
          return throwError(() => new Error('Failed to delete categoria'));
        })
      );
  }
}
