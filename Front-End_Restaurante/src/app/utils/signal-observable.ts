import { Signal, effect } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

// Adapts an Angular Signal to an RxJS Observable that emits on value changes.
// Important: this creates the internal `effect` at the time `signalToObservable`
// is called (i.e. in an injection context when used in a service field
// initializer). That avoids calling `effect()` inside a subscribe callback
// (which would cause NG0203).
export function signalToObservable<T>(sig: Signal<T>): Observable<T> {
  // Use BehaviorSubject so subscribers immediately get the current value
  const subj = new BehaviorSubject<T>(sig());

  // Create effect immediately (must be called in injection context)
  const destroy = effect(() => {
    try {
      subj.next(sig());
    } catch (e) {
      // BehaviorSubject doesn't have error channel here; ignore
      // but you could call subj.error(e as any) if desired
    }
  });

  // We return the subject as an observable. Note: we intentionally do not
  // destroy the Angular effect when someone unsubscribes because the effect
  // lifecycle should be tied to the owner service/component injection lifetime.
  // If you need teardown, wrap subj.asObservable() with custom logic.
  return subj.asObservable();
}
