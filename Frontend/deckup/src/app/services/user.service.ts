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

  codeUrl = this.apiURL + "/code/"

  constructor(private http: HttpClient) { 
    super()
  }

  login(data: FormData) : Observable<any>{
    return this.http.post(`${this.authApiURL}login`, data).pipe(
      catchError(err => {throw err})
    )
  }

  findCards(id: any): Observable<any> {
    return this.http.get(`${this.userApiURL}getcartas/${id}`).pipe(
      catchError(err => {throw err})
    )
  }

  findGames(id: any): Observable<any> {
    return this.http.get(`${this.userApiURL}getgames/${id}`).pipe(
      catchError(err => {throw err})
    )
  }

  findFriends(id: any): Observable<any> {
    return this.http.get(`${this.userApiURL}getfriends/${id}`).pipe(
      catchError(err => {throw err})
    )
  }

  findTrades(id: any): Observable<any> {
    return this.http.get(`${this.userApiURL}gettrades/${id}`).pipe(
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

  claimCode(code: string): Observable<any>{
    const data: FormData = new FormData()
    data.append("user_auth", UserSession.getUser().auth)
    data.append("code", code)
    return this.http.post(`${this.codeUrl}`, data).pipe(
      catchError(err => {throw err})
    )
  }

  getForChat(username: string): Observable<any>{
    return this.http.get(`${this.apiURL}/users/getForChat/${username}`)
  }

  losegame(game: any): Observable<any>{
    const data: FormData = new FormData()
    data.append("user_id", game)
    data.append("user_auth", UserSession.getUser().auth)
    return this.http.post(`${this.apiURL}/game/disconnect`, data).pipe(
      
      catchError(err => {throw err})
    )
  }
  changePFP(auth: any, pfp: any) {
     const data: FormData = new FormData()
    data.append("user_auth", auth)
    data.append("code", pfp)
    return this.http.post(`${this.apiURL}/users/changepfp`, data).pipe(
      
      catchError(err => {throw err})
    )
  }
  changeUsername(newuser: any) {
     const data: FormData = new FormData()
    data.append("user_auth", UserSession.getUser().auth)
    data.append("code", newuser)
    return this.http.post(`${this.apiURL}/users/changeusername`, data).pipe(
      
      catchError(err => {throw err})
    )
  }
}
