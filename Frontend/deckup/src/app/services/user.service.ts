import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  authApiURL = "http://localhost:8888/auth/"

  constructor(private http: HttpClient) { }

  login(data: FormData) : Observable<any>{
    return this.http.post(`${this.authApiURL}login`, data).pipe(
      catchError(err => {throw err})
    )
  }
}
