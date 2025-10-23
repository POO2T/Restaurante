import { Component, signal, ChangeDetectionStrategy, inject } from '@angular/core';
import { AuthService } from './services/auth/auth.service';
import { RouterOutlet } from '@angular/router';
import { Header } from './layout/header/header';
import { Footer } from "./layout/footer/footer";

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Header, Footer],
  templateUrl: './app.html',
  styleUrls: ['./app.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})

export class App {
  protected baseUrl = 'http://localhost:8000';
  private authService = inject(AuthService);

  constructor() {
    // Initialize authentication state early in the app lifecycle
    try { this.authService.init(); } catch (e) { /* ignore errors during SSR */ }
  }
}
