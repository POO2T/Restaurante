import { inject, InjectionToken } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { PLATFORM_ID } from '@angular/core';

export class StorageService {
  private platformId: Object = inject(PLATFORM_ID as unknown as InjectionToken<Object>);

  isBrowser(): boolean {
    return isPlatformBrowser(this.platformId);
  }

  getItem(key: string): string | null {
    if (!this.isBrowser()) return null;
    try {
      return localStorage.getItem(key);
    } catch (e) {
      console.error('StorageService.getItem error', e);
      return null;
    }
  }

  setItem(key: string, value: string): void {
    if (!this.isBrowser()) return;
    try {
      localStorage.setItem(key, value);
    } catch (e) {
      console.error('StorageService.setItem error', e);
    }
  }

  removeItem(key: string): void {
    if (!this.isBrowser()) return;
    try {
      localStorage.removeItem(key);
    } catch (e) {
      console.error('StorageService.removeItem error', e);
    }
  }
}
