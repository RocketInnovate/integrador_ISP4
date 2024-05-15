import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ContactService {

  contactUrl = environment.apiUrl +'account/contact/';

  constructor(private http: HttpClient) { }

  sendMessage(mensaje: any): Observable<any> {
    return this.http.post<any>(this.contactUrl, mensaje);
  }

}
