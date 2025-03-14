import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable } from 'rxjs';
import { UserSession } from '../utils/UserSession';
import { environmentsURLs } from '../utils/environmentsURls';

@Injectable({
  providedIn: 'root'
})
export class UserService extends environmentsURLs {

  authApiURL = this.apiURL + "/auth/"

  userApiURL = this.apiURL + "/users/"

  constructor(private http: HttpClient) { 
    super()
  }

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

  restoreUser(id: number, auth: string) : Observable<any>{
    const data: FormData = new FormData()
    data.append("id", id + "");
    data.append("auth", auth)
    return this.http.post(`${this.authApiURL}restore`, data).pipe(
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
