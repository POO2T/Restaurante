import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
// Certifique-se que o caminho do AuthService está correto para o seu projeto
import { AuthService } from '../../services/auth/auth.service'; 

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  if (token) {
    // Clona a requisição para adicionar o cabeçalho Authorization
    const authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    // Passa a requisição com o token para frente
    return next(authReq);
  }

  // Se não tiver token, passa a requisição original
  return next(req);
};