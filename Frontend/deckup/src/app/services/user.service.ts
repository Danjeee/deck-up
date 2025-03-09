import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable } from 'rxjs';
import { UserSession } from '../utils/UserSession';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  authApiURL = "https://localhost:8888/auth/"

  userApiURL = "https://localhost:8888/users/"

  constructor(private http: HttpClient) { }

  login(data: FormData) : Observable<any>{
    return this.http.post(`${this.authApiURL}login`, data).pipe(
      catchError(err => {throw err})
    )
  }

  logout() : Observable<any>{
    return this.http.post(`${this.authApiURL}logout`, null).pipe(
      catchError(err => {throw err})
    )
  }

  findById(id: number) : Observable<any>{
    return this.http.get(`${this.userApiURL}${id}`).pipe(
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

  getPaid() : Observable<any>{
    const data: FormData = new FormData()
    data.append("id", UserSession.getId())
    return this.http.post(`${this.userApiURL}getPaid`, data).pipe(
      catchError(err => {throw err})
    )
  }
}
