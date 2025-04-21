import { Injectable } from '@angular/core';
import { environmentsURLs } from '../utils/environmentsURls';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, catchError, Observable } from 'rxjs';
import { UserSession } from '../utils/UserSession';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

@Injectable({
  providedIn: 'root'
})
export class GameService extends environmentsURLs {

  websocketURL = `${this.apiURL}/ws`

  gameURL = `${this.apiURL}/game`

  private stompClient: any;

  private status: BehaviorSubject<any> = new BehaviorSubject<any>("")

  constructor(private http: HttpClient) {
    super()
  }
  initConectionSocket() {
    const socket = new SockJS(this.websocketURL)
    this.stompClient = Stomp.over(socket)
    // this.stompClient.debug = () => { }
  }

  joinListener(game: any) {
    this.initConectionSocket()
    this.status = new BehaviorSubject<any>("")
    try {
      this.stompClient.connect({}, () => {
        this.stompClient.subscribe(`/game/${game}`, (messages: any) => {
          const ur = messages.body
          this.status.next(ur)
        });
      })
    } catch (error) {
      this.stompClient.disconnect()
    }
  }

  getstatus() {
    return this.status.asObservable();
  }

  disconnect() {
    this.stompClient.disconnect()
  }

  getGame(id: any): Observable<any> {
    const data: FormData = new FormData()
    data.append("id", id)
    return this.http.post(`${this.gameURL}/get`, data).pipe(
      catchError(err => { throw err })
    )
  }

  put(linea: any, card: any, game: any, player: any): Observable<any>{
    const data: FormData = new FormData()
    data.append("game_id", game)
    data.append("card_id", card)
    data.append("player", player)
    data.append("linea", linea)
    return this.http.post(`${this.gameURL}/put`, data).pipe(
      catchError(err => {throw err})
    )
  }

  selfspell(linea: any, card: any, game: any, player: any): Observable<any>{
    const data: FormData = new FormData()
    data.append("game_id", game)
    data.append("card_id", card)
    data.append("player", player)
    data.append("linea", linea)
    return this.http.post(`${this.gameURL}/selfspell`, data).pipe(
      catchError(err => {throw err})
    )
  }

  switchturn(game: any, player :any): Observable<any>{
    const data: FormData = new FormData()
    data.append("game_id", game)
    data.append("player", player)
    return this.http.post(`${this.gameURL}/switch`, data).pipe(
      
      catchError(err => {throw err})
    )
  }

  lose(game: any): Observable<any>{
    const data: FormData = new FormData()
    data.append("user_id", game)
    data.append("user_auth", UserSession.getUser().auth)
    return this.http.post(`${this.gameURL}/disconnect`, data).pipe(
      
      catchError(err => {throw err})
    )
  }
}
