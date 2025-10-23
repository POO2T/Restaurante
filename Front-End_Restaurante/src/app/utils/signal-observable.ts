import { Signal, effect } from '@angular/core';
import { Observable } from 'rxjs';

// Adapts an Angular Signal to an RxJS Observable that emits on value changes.
export function signalToObservable<T>(sig: Signal<T>): Observable<T> {
  return new Observable<T>(subscriber => {
    // Emit current value
    try {
      subscriber.next(sig());
    } catch (e) {
      subscriber.error(e as unknown);
    }

    // Create an Angular effect to push updates whenever the signal changes
    const destroy: unknown = effect(() => {
      try {
        subscriber.next(sig());
      } catch (e) {
        subscriber.error(e as unknown);
      }
    });

    return () => {
      // Stop the effect when unsubscribed
      try {
        type DestroyFn = (() => void) | { destroy?: () => void } | null | undefined;
        const d = destroy as DestroyFn;
        if (typeof d === 'function') d();
        else if (d && typeof d.destroy === 'function') d.destroy();
      } catch {
        // ignore cleanup errors
      }
    };
  });
}
