import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class SidebarService {
  private _isOpen = signal<boolean>(false);
  readonly isOpen = this._isOpen;

  constructor() { }

  toggle(): void { this._isOpen.set(!this._isOpen()); }
  close(): void { this._isOpen.set(false); }
  open(): void { this._isOpen.set(true); }
}
