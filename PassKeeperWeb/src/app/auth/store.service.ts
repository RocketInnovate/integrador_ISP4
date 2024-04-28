import { Injectable } from '@angular/core';
import { JwtTokens, User } from './auth.models';

@Injectable({
  providedIn: 'root'
})
export class StoreService {

  constructor() { }

  setUser(user: User){
    localStorage.setItem('user', JSON.stringify(user));
  }
  setAccessToken(token: string){
    localStorage.setItem('access_token', token);
  }
  setRefreshToken(token: string){
    localStorage.setItem('refresh_token', token);
  }

  

  getUser(){
    return localStorage.getItem('user');
  }
   
  getAccessToken(): string {
    return localStorage.getItem('access_token')||'';
  }
  getRefreshToken(): string {
    return localStorage.getItem('refresh_token')||'';
  }
  clearLocalStorage(): void {
    
    localStorage.removeItem('user');
    localStorage.removeItem('refresh_token');
    localStorage.removeItem('access_token');
    
    
  }
}
