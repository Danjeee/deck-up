import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  authApiURL = "https://localhost:8888/auth/"

  constructor(private http: HttpClient) { }

  login(data: FormData) : Observable<any>{
    return this.http.post(`${this.authApiURL}login`, data).pipe(
      catchError(err => {throw err})
    )
  }

  addVerification(mail: string) : Observable<any>{
    const data: FormData = new FormData
    data.append("email", mail)
    return this.http.post(`${this.authApiURL}verify/${mail}`, data).pipe(
      catchError(err => {throw err})
    )
  }

  verify(data: FormData) : Observable<any>{
    return this.http.post(`${this.authApiURL}verify`, data).pipe(
      catchError(err => {throw err})
    )
  }

  register(data: FormData, code: string) : Observable<any>{
    return this.http.post(`${this.authApiURL}register/verify/${code}`, data).pipe(
      catchError(err => {throw err})
    )
  }
}
