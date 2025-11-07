<<<<<<< HEAD
import { Component } from '@angular/core';

@Component({
  selector: 'app-header',
  imports: [],
  templateUrl: './header.html',
  styleUrl: './header.css'
})
export class Header {

=======
import { Component, ChangeDetectionStrategy, inject, signal, OnDestroy } from '@angular/core';
import { RouterModule, ActivatedRoute, Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/internal/operators/filter';
import { Subscription } from 'rxjs';

import { AuthGuard } from '../../guards/auth.guard';
import { AuthService } from '../../services/auth/auth.service';

@Component({
  selector: 'app-header',
  imports: [RouterModule],
  templateUrl: './header.html',
  styleUrls: ['./header.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class Header {

  isAuthenticated = signal<boolean>(false);
  userRole = signal<'CLIENTE' | 'FUNCIONARIO' | null>(null);
  rotaAtual: string = '';
  isFuncionarioRoute: boolean = false;
  isNavOpen: boolean = false;


  private router = inject(Router);
  private activatedRoute = inject(ActivatedRoute);
  private authGuard = inject(AuthGuard);
  private authService = inject(AuthService);
  private subs: Subscription[] = [];
  
  constructor() {}
  
  ngOnInit(): void {

    // Inicializa com o estado atual
    this.isAuthenticated.set(this.authService.isAuthenticated());
    this.userRole.set(this.authService.userType());

    // Assina mudanças reativas do AuthService para atualizar o header sem reload
    this.subs.push(
      this.authService.isAuthenticated$.subscribe((v: boolean) => {
        this.isAuthenticated.set(v);
      })
    );

    this.subs.push(
      this.authService.userType$.subscribe((t: 'CLIENTE' | 'FUNCIONARIO' | null) => {
        this.userRole.set(t);
      })
    );

    this.subs.push(
      this.router.events.pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd))
        .subscribe((event: NavigationEnd) => {
          this.rotaAtual = event.urlAfterRedirects;
          this.isFuncionarioRoute = this.rotaAtual.includes('/funcionario');
        })
    );
  }

  ngOnDestroy(): void {
    this.subs.forEach((s) => s.unsubscribe());
  }

  
  toggleNav(): void {
    this.isNavOpen = !this.isNavOpen;
  }
  closeNav(): void {
    this.isNavOpen = false;
  }

  logout(): void {
    this.authService.logout();
    this.isAuthenticated.set(false);
    this.router.navigate(['/']);
  }
>>>>>>> master-robert
}
