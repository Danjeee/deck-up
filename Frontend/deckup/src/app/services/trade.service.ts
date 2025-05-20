import { Injectable } from '@angular/core';
import { environmentsURLs } from '../utils/environmentsURls';
import { BehaviorSubject, catchError, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { UserService } from './user.service';
import { UserSession } from '../utils/UserSession';
import { Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

@Injectable({
  providedIn: 'root'
})
export class TradeService extends environmentsURLs {
  websocketURL = `${this.apiURL}/ws`;
  tradeURL = `${this.apiURL}/trades`;
  cardURL = `${this.apiURL}/cards`;

  private stompClient: any;
  private isConnected = false;
  private reconnectDelay = 3000;
  private tradeTopic: string = '';
  private status: BehaviorSubject<any> = new BehaviorSubject<any>('');
  constructor(private http: HttpClient) {
    super()
  }

  sendFinish(trade: any): Observable<any>{
    const data: FormData = new FormData()
    data.append("user_id", trade)
    return this.http.post(`${this.tradeURL}/sfinish`, data).pipe(
      catchError(err => { throw err })
    ) 
  }

  finish(trade: any): Observable<any>{
    const data: FormData = new FormData()
    data.append("user_id", trade)
    return this.http.post(`${this.tradeURL}/finish`, data).pipe(
      catchError(err => { throw err })
    ) 
  }

  create(): Observable<any> {
    const data: FormData = new FormData()
    data.append("user_auth", UserSession.getUser().auth)
    return this.http.post(`${this.tradeURL}/new`, data).pipe(
      catchError(err => { throw err })
    )
  }

  nope(id: any): Observable<any> {
    const data: FormData = new FormData()
    data.append("user_auth", UserSession.getUser().auth)
    data.append("user_id", id)
    return this.http.post(`${this.tradeURL}/unaccept`, data).pipe(
      catchError(err => { throw err })
    )
  }

  accept(id: any): Observable<any> {
    const data: FormData = new FormData()
    data.append("user_auth", UserSession.getUser().auth)
    data.append("user_id", id)
    return this.http.post(`${this.tradeURL}/accept`, data).pipe(
      catchError(err => { throw err })
    )
  }
  add(id: any, cant: any, trade: any): Observable<any> {
    const data: FormData = new FormData()
    data.append("user_auth", UserSession.getUser().auth)
    data.append("artifact_aux", id)
    data.append("artifact_id", cant)
    data.append("artifact_long", trade)
    return this.http.post(`${this.tradeURL}/add`, data).pipe(
      catchError(err => { throw err })
    )
  }

  addcurr(cant: any, trade: any): Observable<any> {
    const data: FormData = new FormData()
    data.append("user_auth", UserSession.getUser().auth)
    data.append("artifact_id", cant)
    data.append("artifact_long", trade)
    return this.http.post(`${this.tradeURL}/addgems`, data).pipe(
      catchError(err => { throw err })
    )
  }

  remove(id: any, trade: any): Observable<any> {
    const data: FormData = new FormData()
    data.append("user_auth", UserSession.getUser().auth)
    data.append("user_id", id)
    data.append("artifact_long", trade)
    return this.http.post(`${this.tradeURL}/remove`, data).pipe(
      catchError(err => { throw err })
    )
  }

  cancel(trade: any = null): Observable<any> {
    const data: FormData = new FormData()
    data.append("user_auth", UserSession.getUser().auth)
    data.append("user_id", sessionStorage.getItem("trade") as string)
    if (trade != null){
      data.delete("user_id")
      data.append("user_id", trade)
    }
    return this.http.post(`${this.tradeURL}/leave`, data).pipe(
      catchError(err => { throw err })
    )
  }

  getTrade(id: any): Observable<any> {
    const data: FormData = new FormData()
    data.append("user_auth", UserSession.getUser().auth)
    return this.http.post(`${this.tradeURL}/${id}`, data).pipe(
      catchError(err => { throw err })
    )
  }

  findAllPlayerCards(): Observable<any> {
    const data: FormData = new FormData()
    data.append("user_auth", UserSession.getUser().auth)
    return this.http.post(`${this.cardURL}/getByPlayer`, data).pipe(
      catchError(err => { throw err })
    )
  }

  findAllOtherPlayerCards(id: any): Observable<any> {
    return this.http.get(`${this.cardURL}/getOf/${id}`).pipe(
      catchError(err => { throw err })
    )
  }

  pedir(trade: any,carta: any, gemas: any = 0): Observable<any> {
    const data: FormData = new FormData()
    data.append("user_auth", UserSession.getUser().auth)
    data.append("artifact_aux", gemas)
    data.append("artifact_long", carta.id)
    data.append("user_id", trade)
    return this.http.post(`${this.tradeURL}/msg`, data).pipe(
      catchError(err => { throw err })
    )
  }

  join(code: any): Observable<any> {
    const data: FormData = new FormData()
    data.append("user_auth", UserSession.getUser().auth)
    data.append("code", code)
    return this.http.post(`${this.tradeURL}/join`, data).pipe(
      catchError(err => { throw err })
    )
  }

  private initConnectionSocket(): void {
    const socket = new SockJS(this.websocketURL);
    this.stompClient = Stomp.over(socket);
    this.stompClient.debug = () => { };
  }

  private reconnect(): void {
    console.warn('Intentando reconectar WebSocket...');
    setTimeout(() => {
      if (this.tradeTopic) {
        this.joinListener(this.tradeTopic);
      }
    }, this.reconnectDelay);
  }

  joinListener(tradeId: string): void {
    this.tradeTopic = tradeId;
    this.status = new BehaviorSubject<any>('');

    if (!this.stompClient || !this.isConnected) {
      this.initConnectionSocket();
    }

    try {
      this.stompClient.connect({}, () => {
        this.isConnected = true;
        console.log('WebSocket conectado');
        this.stompClient.subscribe(`/trade/${tradeId}`, (message: any) => {
          this.status.next(message.body);
        });
      }, (error: any) => {
        console.error('Error en conexión WebSocket:', error);
        this.isConnected = false;
        this.reconnect();
      });
    } catch (error) {
      console.error('Error durante la conexión:', error);
      this.disconnect();
      this.reconnect();
    }
  }

  getStatus(): Observable<any> {
    return this.status.asObservable();
  }

  disconnect(): void {
    if (this.stompClient && this.isConnected) {
      this.stompClient.disconnect(() => {
        console.log('WebSocket desconectado');
      });
    }
    this.isConnected = false;
  }

}
