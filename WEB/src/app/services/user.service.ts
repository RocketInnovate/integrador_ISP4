import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { User } from '../shared/models/User.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient) { }

  getUsers(): Observable<User[]> {
    const url = `${environment.apiUrl}/admin/users/`;
    return this.http.get<User[]>(url)
      .pipe(
        tap(_ => console.log('fetched users')),
        catchError(this.handleError<User[]>('getUsers'))
      );
  }

  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.error(error); // log the error for future debugging

      // Return a safe result or an empty observable with a user-facing error message
      return of(result as T);
    };
  }
}