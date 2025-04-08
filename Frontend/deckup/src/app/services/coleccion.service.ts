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
  saveDeck(deck: any[],nombre: string, id:any = null): Observable<any>{
    var data;
    if (id == null){
      data = {
        nombre: nombre,
        carta1: deck[0],
        carta2: deck[1],
        carta3: deck[2],
        carta4: deck[3],
        carta5: deck[4],
        carta6: deck[5],
        carta7: deck[6],
        carta8: deck[7],
        usuario: {
          auth: UserSession.getUser().auth
        }
      }
    } else {
      data = {
        id: id,
        nombre: nombre,
        carta1: deck[0],
        carta2: deck[1],
        carta3: deck[2],
        carta4: deck[3],
        carta5: deck[4],
        carta6: deck[5],
        carta7: deck[6],
        carta8: deck[7],
        usuario: {
          auth: UserSession.getUser().auth
        }
      }
    }
    return this.http.post(`${this.deckUrl}/save`, data).pipe(
      catchError(err => {throw err})
    )
  }
  findDeck(id: any): Observable<any>{
    return this.http.get(`${this.deckUrl}/${id}`).pipe(
      catchError(err => {throw err})
    )
  }
  deleteDeck(id: any): Observable<any>{
    const data: FormData = new FormData()
    data.append("user_id", id)
    data.append("user_auth", UserSession.getUser().auth)
    return this.http.post(`${this.deckUrl}/delete`, data).pipe(
      catchError(err => {throw err})
    )
  }

  select(id: any): Observable<any>{
    const data: FormData = new FormData()
    data.append("user_id", id)
    data.append("user_auth", UserSession.getUser().auth)
    return this.http.post(`${this.deckUrl}/select`, data).pipe(
      catchError(err => {throw err})
    )
  }
}
