import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
;
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PaymentsService {

  // // Endpoint de payments
  // private url: String = environment.apiUrl+'/payments/'

  constructor(private http: HttpClient) { }

  // Método para obtener payments
  getPayments() {
    return this.http.get(environment.apiUrl+'/payments/');
  }


}
