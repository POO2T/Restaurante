import { Component, ChangeDetectionStrategy, inject } from '@angular/core';
import { RouterModule, ActivatedRoute, Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/internal/operators/filter';

@Component({
  selector: 'app-header',
  imports: [RouterModule],
  templateUrl: './header.html',
  styleUrls: ['./header.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class Header {

  rotaAtual: string = '';
  isFuncionarioRoute: boolean = false;
  isNavOpen: boolean = false;

  private router = inject(Router);
  private activatedRoute = inject(ActivatedRoute);
  
  constructor() {}
  
  ngOnInit(): void {
    //this.rotaAtual = this.activatedRoute.snapshot.url.join('/');

    this.router.events.pipe(
      filter((event): event is NavigationEnd => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      this.rotaAtual = event.urlAfterRedirects;
      this.isFuncionarioRoute = this.rotaAtual.includes("/funcionario");
      console.log(this.rotaAtual);
      console.log(this.isFuncionarioRoute);
    });
  }

  
  toggleNav() {
    this.isNavOpen = !this.isNavOpen;
  }
  closeNav() {
    this.isNavOpen = false;
  }

}
