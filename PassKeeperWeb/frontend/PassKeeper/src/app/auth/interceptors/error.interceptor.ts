import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse,

} from '@angular/common/http';
import { Observable, catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from '../auth.service';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

  constructor(private authService: AuthService, ) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          // Token posiblemente vencido, intenta refrescarlo
          return this.handle401Error(request, next);
        }
        // Manejar otros tipos de errores aquí si es necesario
        return throwError(() => error);
      })
    );
  }

  private handle401Error(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Suponiendo que refreshToken() retorna un nuevo token de acceso
    return this.authService.refreshToken().pipe(
      switchMap((tokens: any) => {
        // Guardar el nuevo token de acceso aquí si es necesario
        const newRequest = request.clone({
          setHeaders: {
            Authorization: `Bearer ${tokens.access}`
          }
        });
        return next.handle(newRequest);
      }),
      catchError((err) => {
        // Si refrescar el token también falla, entonces desloguearse
        this.authService.logOut();
        return throwError(() => err);
      })
    );
  }

 
}
