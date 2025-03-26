import { Injectable } from '@angular/core';
import { environmentsURLs } from '../utils/environmentsURls';
import { catchError, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { UserSession } from '../utils/UserSession';

@Injectable({
  providedIn: 'root'
})
export class ColeccionService extends environmentsURLs {

  deckUrl = `${this.apiURL}/decks`
  cardUrl = `${this.apiURL}/cards`

  constructor(private http: HttpClient) {
    super()
  }

  findAllCards(): Observable<any>{
    return this.http.get(`${this.cardUrl}/all`).pipe(
      catchError(err => {throw err})
    )
  }
  findAllPlayerCards(): Observable<any>{
    const data: FormData = new FormData()
    data.append("user_auth", UserSession.getUser().auth)
    return this.http.post(`${this.cardUrl}/getByPlayer`, data).pipe(
      catchError(err => {throw err})
    )
  }
  findPlayerDecks(): Observable<any>{
    const data: FormData = new FormData()
    data.append("auth", UserSession.getUser().auth)
    return this.http.post(`${this.deckUrl}/all`, data).pipe(
      catchError(err => {throw err})
    )
  }
  findRarezas(): Observable<any>{
    return this.http.get(`${this.cardUrl}/rarezas`).pipe(
      catchError(err => {throw err})
    )
  }
}
