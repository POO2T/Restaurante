import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StorageService } from './storage.service';

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  private baseUrl = 'http://localhost:8080/api'; // Ajuste conforme seu backend
  private http = inject(HttpClient);
  private storage = inject(StorageService);

  private getHeaders(): HttpHeaders {
    let token = this.storage.getItem('auth_token');
    // Fallback: read directly from window.localStorage if StorageService returned null
    if (!token && typeof window !== 'undefined' && window?.localStorage) {
      try {
        token = window.localStorage.getItem('auth_token');
      } catch (e) {
        // ignore
      }
    }

    // Debug: log presence of token and a short redacted preview (do not leak full token)
    console.debug(
      'ApiService.getHeaders token present?',
      !!token,
      token ? `${token.substring(0, 10)}...` : null
    );

    return new HttpHeaders({
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    });
  }

  get<T>(endpoint: string): Observable<T> {
    return this.http.get<T>(`${this.baseUrl}${endpoint}`, {
      headers: this.getHeaders(),
    });
  }

  post<T, B = unknown>(endpoint: string, data: B): Observable<T> {
    return this.http.post<T>(`${this.baseUrl}${endpoint}`, data, {
      headers: this.getHeaders(),
    });
  }

  put<T, B = unknown>(endpoint: string, data: B): Observable<T> {
    return this.http.put<T>(`${this.baseUrl}${endpoint}`, data, {
      headers: this.getHeaders(),
    });
  }

  delete<T>(endpoint: string): Observable<T> {
    return this.http.delete<T>(`${this.baseUrl}${endpoint}`, {
      headers: this.getHeaders(),
    });
  }
}
