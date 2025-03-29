import { Injectable } from '@angular/core';
import { environmentsURLs } from '../utils/environmentsURls';
import { HttpClient } from '@angular/common/http';
import { UserSession } from '../utils/UserSession';
import { catchError, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FriendService extends environmentsURLs{

  friendURL = `${this.apiURL}/friendlist/`

  constructor(private http: HttpClient) {
    super()
  }

  getFriendList() : Observable<any>{
    const data: FormData = new FormData()
    data.append("user_auth", UserSession.getUser().auth)
    return this.http.post(this.friendURL, data).pipe(
      catchError(err => {throw err})
    )
  }

  deletefriend(id: any) : Observable<any>{
    const data: FormData = new FormData()
    data.append("user_auth", UserSession.getUser().auth)
    data.append("user_id", id)
    return this.http.post(`${this.friendURL}delete`, data).pipe(
      catchError(err => {throw err})
    )
  }
  
  acceptreq(id: any) : Observable<any>{
    const data: FormData = new FormData()
    data.append("user_auth", UserSession.getUser().auth)
    data.append("user_id", id)
    return this.http.post(`${this.friendURL}accept`, data).pipe(
      catchError(err => {throw err})
    )
  }

  declinereq(id: any) : Observable<any>{
    const data: FormData = new FormData()
    data.append("user_auth", UserSession.getUser().auth)
    data.append("user_id", id)
    return this.http.post(`${this.friendURL}decline`, data).pipe(
      catchError(err => {throw err})
    )
  }

  sendreq(username: any) : Observable<any>{
    const data: FormData = new FormData()
    data.append("user_auth", UserSession.getUser().auth)
    data.append("code", username)
    return this.http.post(`${this.friendURL}add`, data).pipe(
      catchError(err => {throw err})
    )
  }
}
