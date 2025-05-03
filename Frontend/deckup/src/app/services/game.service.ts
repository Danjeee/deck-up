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
  websocketURL = `${this.apiURL}/ws`;
  gameURL = `${this.apiURL}/game`;

  private stompClient: any;
  private isConnected = false;
  private reconnectDelay = 3000;
  private gameTopic: string = '';
  private status: BehaviorSubject<any> = new BehaviorSubject<any>('');

  constructor(private http: HttpClient) {
    super();
  }

  private initConnectionSocket(): void {
    const socket = new SockJS(this.websocketURL);
    this.stompClient = Stomp.over(socket);
    this.stompClient.debug = () => {};
  }

  private reconnect(): void {
    console.warn('Intentando reconectar WebSocket...');
    setTimeout(() => {
      if (this.gameTopic) {
        this.joinListener(this.gameTopic);
      }
    }, this.reconnectDelay);
  }

  joinListener(gameId: string): void {
    this.gameTopic = gameId;
    this.status = new BehaviorSubject<any>('');

    if (!this.stompClient || !this.isConnected) {
      this.initConnectionSocket();
    }

    try {
      this.stompClient.connect({}, () => {
        this.isConnected = true;
        console.log('WebSocket conectado');
        this.stompClient.subscribe(`/game/${gameId}`, (message: any) => {
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

  getGame(id: any): Observable<any> {
    const data = new FormData();
    data.append('id', id);
    return this.http.post(`${this.gameURL}/get`, data).pipe(
      catchError(err => { throw err; })
    );
  }

  put(linea: any, card: any, game: any, player: any): Observable<any> {
    const data = new FormData();
    data.append('game_id', game);
    data.append('card_id', card);
    data.append('player', player);
    data.append('linea', linea);
    return this.http.post(`${this.gameURL}/put`, data).pipe(
      catchError(err => { throw err; })
    );
  }

  selfspell(linea: any, card: any, game: any, player: any): Observable<any> {
    const data = new FormData();
    data.append('game_id', game);
    data.append('card_id', card);
    data.append('player', player);
    data.append('linea', linea);
    return this.http.post(`${this.gameURL}/selfspell`, data).pipe(
      catchError(err => { throw err; })
    );
  }

  spellthrow(linea: any, card: any, game: any, player: any): Observable<any> {
    const data = new FormData();
    data.append('game_id', game);
    data.append('card_id', card);
    data.append('player', player);
    data.append('linea', linea);
    return this.http.post(`${this.gameURL}/spellthrow`, data).pipe(
      catchError(err => { throw err; })
    );
  }

  switchturn(game: any, player: any): Observable<any> {
    const data = new FormData();
    data.append('game_id', game);
    data.append('player', player);
    return this.http.post(`${this.gameURL}/switch`, data).pipe(
      catchError(err => { throw err; })
    );
  }

  lose(game: any): Observable<any> {
    const data = new FormData();
    data.append('user_id', game);
    data.append('user_auth', UserSession.getUser().auth);
    return this.http.post(`${this.gameURL}/disconnect`, data).pipe(
      catchError(err => { throw err; })
    );
  }
}
