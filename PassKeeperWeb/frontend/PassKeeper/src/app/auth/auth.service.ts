import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse} from '@angular/common/http';
import { JwtTokens, LoginCredentials, LoginResponse } from './auth.models';
import { BehaviorSubject, Observable, catchError, tap, throwError } from 'rxjs';
import {enviroment} from '../../../enviroment/enviroment';
import { StoreService } from './store.service';



@Injectable({
  providedIn: 'root'
})
export class AuthService {
  loginUrl=enviroment.api_Url +'account/login/';
  refreshTokenUrl=enviroment.api_Url+'account/refresh/';
  logoutUrl = enviroment.api_Url + 'account/logout/';
  
  private islogged = new BehaviorSubject<boolean>(false); 

  constructor(private http: HttpClient, private storeService: StoreService) {
    this.checkToken()
  }
  
  logIn(credentials:LoginCredentials):Observable<LoginResponse> {
    return  this.http.post<LoginResponse>(this.loginUrl, credentials)
  }

  // logOut(){
  //   this.storeService.clearLocalStorage()
  //   this.islogged.next(false);
  // }
  logOut(): Observable<any> {
    const refreshToken = { refresh: this.storeService.getRefreshToken() };

    if (!refreshToken.refresh) {
      // No hay refresh token, así que simplemente limpia el local storage y actualiza el estado de autenticación
      this.storeService.clearLocalStorage();
      this.islogged.next(false);
      return throwError(() => new Error('Refresh token no disponible'));
    }

    return this.http.post(this.logoutUrl, refreshToken).pipe(
      tap(() => {
        // Limpia el local storage y actualiza el estado de autenticación después del logout exitoso
        this.storeService.clearLocalStorage();
        this.islogged.next(false);
      }),
      catchError((error) => {
        // Manejo de errores
        this.storeService.clearLocalStorage();
        this.islogged.next(false);
        console.error('Logout error', error);
        return throwError(() => new Error('Error durante el logout'));
      })
    );
  }
  
  refreshToken(): Observable<HttpResponse<JwtTokens>> {
    const refreshToken = {"refresh":this.storeService.getRefreshToken()};
    console.log(refreshToken)
    if (!refreshToken) {
      this.logOut();
      return throwError(() => new Error('Refresh token no disponible'));
    }

    return this.http.post<HttpResponse<JwtTokens>>(this.refreshTokenUrl,refreshToken)
      
  }
  setIsLogged(islogged:boolean){
    this.islogged.next(islogged);
  }

  getIsLogged(): Observable<boolean> {
    return this.islogged.asObservable();
  }
  
  checkToken() {
    this.refreshToken().subscribe({
      next:(response:any) => {
        console.log(response.access)
        const access=(response.access)
        const refresh=response.refresh
        if (access && refresh ) {
          this.storeService.setAccessToken(access)
          this.storeService.setRefreshToken(refresh)
          this.islogged.next(true)
          console.log('refresh successful');
          
        } else {
          this.logOut()
          // Maneja el caso donde no hay tokens en la respuesta.
          console.error('Invalid credentials', response);
        }
      },
      error: (err) => {
        this.logOut()
        let errorMessage = 'An error occurred during login.';
        if (err.status === 400) {
          // Puedes obtener el mensaje de error de la respuesta del backend aquí
          errorMessage = err.error.message || 'Credentials are incorrect.';
        }
        console.error(errorMessage);
        // Aquí podrías mostrar el mensaje de error en la interfaz de usuario
        
      }
    })
   
  }
}
