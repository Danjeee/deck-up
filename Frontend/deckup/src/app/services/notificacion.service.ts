import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, Observable } from 'rxjs';
import { environmentsURLs } from '../utils/environmentsURls';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import { UserSession } from '../utils/UserSession';

@Injectable({
  providedIn: 'root'
})
export class NotificacionService extends environmentsURLs {
  chatURL = `${this.apiURL}/ws`
  notifURL = `${this.apiURL}/notifs`

  private stompClient: any;

  private notifications: BehaviorSubject<any> = new BehaviorSubject<any>("")

  constructor(private http: HttpClient) {
    super()
    this.initConectionSocket()
  }
  initConectionSocket() {
    const socket = new SockJS(this.chatURL)
    this.stompClient = Stomp.over(socket)
    this.stompClient.debug = ()=>{}
  }

  joinListener() {
    try {
      this.stompClient.connect({}, () => {
        this.stompClient.subscribe(`/topic/notifications/${UserSession.getId()}`, (messages: any) => {
          const ur = messages.body
          this.notifications.next(ur)
        });
      })
    } catch (error) {
      this.stompClient.disconnect()
    }
  }

  getnotifications() {
    return this.notifications.asObservable();
  }

  claimAll(): Observable<any>{
    const data: FormData = new FormData()
    data.append("user_auth", UserSession.getUser().auth)
    return this.http.post(`${this.notifURL}/getUnreaded`, data).pipe(
      catchError(err => {throw err})
    )
  }

  readAll(): Observable<any>{
    const data: FormData = new FormData()
    data.append("user_auth", UserSession.getUser().auth)
    return this.http.post(`${this.notifURL}/readAll`, data).pipe(
      catchError(err => {throw err})
    )
  }
}
