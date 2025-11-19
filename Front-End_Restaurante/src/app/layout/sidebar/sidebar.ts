import { ChangeDetectionStrategy, Component, inject, Injectable, signal, Signal } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AuthGuard } from '../../guards/auth.guard';
import { SidebarService } from '../../services/sidebar/sidebar.service';


@Component({
  selector: 'app-sidebar',
  imports: [RouterModule],
  templateUrl: './sidebar.html',
  styleUrls: ['./sidebar.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class Sidebar {

  // Inject the AuthService to decide whether to show the sidebar
  private authGaurd = inject(AuthGuard);
  private sidebarService = inject(SidebarService);

  //readonly isUser = signal<'CLIENTE' | 'FUNCIONARIO' | null>(null);

  constructor() {}

  // Getter used by the template to conditionally render the sidebar
  get isUserType(): 'CLIENTE' | 'FUNCIONARIO' | null {
    return this.authGaurd.getUserType();
  }

  get isAuthenticated(): boolean {
    return this.authGaurd.isAuthenticated();
  }

  get isSidebarActive(): boolean {
    return this.sidebarService.isOpen();
  }

  get toggleSidebar(): boolean {
    this.sidebarService.toggle();
    return this.sidebarService.isOpen();
  }

  get closeSidebar(): boolean {
    this.sidebarService.close();
    return this.sidebarService.isOpen();
  }
}