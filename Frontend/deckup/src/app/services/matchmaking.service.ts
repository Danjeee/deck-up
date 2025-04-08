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

  matchURL = `${this.apiURL}/ws`

  matchRestURL = `${this.apiURL}/game/matchmaking`

  private stompClient: any;

  private status: BehaviorSubject<any> = new BehaviorSubject<any>("")

  constructor(private http: HttpClient) {
    super()
  }
  initConectionSocket() {
    const socket = new SockJS(this.matchURL)
    this.stompClient = Stomp.over(socket)
    //this.stompClient.debug = () => { }
  }

  startMatch(): Observable<any>{
    const data: FormData = new FormData()
    data.append("auth", UserSession.getUser().auth)
    return this.http.post(`${this.matchRestURL}`, data).pipe(
      catchError(err => {throw err})
    )
  }

  cancel(){
    this.stompClient.disconnect()
    const data: FormData = new FormData()
    data.append("auth", UserSession.getUser().auth)
    this.http.post(`${this.matchRestURL}/cancel`, data).pipe(
      catchError(err => {throw err})
    )
  }

  joinListener() {
    this.initConectionSocket()
    try {
      this.stompClient.connect({}, () => {
        this.stompClient.subscribe(`/matchmaking/${UserSession.getId()}`, (messages: any) => {
          const ur = messages.body
          this.status.next(ur)
          console.log(this.status)
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
    this.status = new BehaviorSubject<any>("");
  }
}
