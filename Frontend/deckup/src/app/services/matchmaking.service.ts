import { Injectable } from '@angular/core';
import { environmentsURLs } from '../utils/environmentsURls';
import { BehaviorSubject, catchError, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import { UserSession } from '../utils/UserSession';

@Injectable({
  providedIn: 'root'
})
export class MatchmakingService extends environmentsURLs {
  matchURL = `${this.apiURL}/ws`;
  matchRestURL = `${this.apiURL}/game/matchmaking`;

  private stompClient: any;
  private isConnected = false;
  private reconnectDelay = 3000;
  private status: BehaviorSubject<any> = new BehaviorSubject<any>('');

  constructor(private http: HttpClient) {
    super();
  }

  private initConnectionSocket(): void {
    const socket = new SockJS(this.matchURL);
    this.stompClient = Stomp.over(socket);
    this.stompClient.debug = () => {};
  }

  private reconnect(): void {
    console.warn('Reintentando conexión WebSocket...');
    setTimeout(() => {
      this.joinListener(); // reinicia conexión y suscripción
    }, this.reconnectDelay);
  }

  joinListener(): void {
    const userId = UserSession.getId();
    this.status = new BehaviorSubject<any>('');

    if (!this.stompClient || !this.isConnected) {
      this.initConnectionSocket();
    }

    try {
      this.stompClient.connect({}, () => {
        this.isConnected = true;
        console.log('Conectado a WebSocket para matchmaking');

        this.stompClient.subscribe(`/matchmaking/${userId}`, (message: any) => {
          this.status.next(message.body);
        });
      }, (error: any) => {
        console.error('Error en conexión WebSocket:', error);
        this.isConnected = false;
        this.reconnect();
      });
    } catch (error) {
      console.error('Excepción en WebSocket:', error);
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
    this.status = new BehaviorSubject<any>(''); // resetear estado
  }

  startMatch(): Observable<any> {
    const data = new FormData();
    data.append('auth', UserSession.getUser().auth);
    return this.http.post(`${this.matchRestURL}`, data).pipe(
      catchError(err => { throw err; })
    );
  }

  cancel(): Observable<any> {
    const data = new FormData();
    data.append('user_auth', UserSession.getUser().auth);
    return this.http.post(`${this.matchRestURL}/cancel`, data).pipe(
      catchError(err => { throw err; })
    );
  }
}