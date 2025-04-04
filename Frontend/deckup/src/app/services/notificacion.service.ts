import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { environmentsURLs } from '../utils/environmentsURls';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import { UserSession } from '../utils/UserSession';

@Injectable({
  providedIn: 'root'
})
export class NotificacionService extends environmentsURLs {
  chatURL = `${this.apiURL}/ws`

  private stompClient: any;

  private requests: BehaviorSubject<any> = new BehaviorSubject<any>("")

  private unreaded: BehaviorSubject<any> = new BehaviorSubject<any>("")

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
        this.stompClient.subscribe(`/topic/requests/${UserSession.getId()}`, (messages: any) => {
          const request = messages.body
          this.requests.next(request)
        });
        this.stompClient.subscribe(`/topic/unreaded/${UserSession.getId()}`, (messages: any) => {
          const ur = messages.body
          this.unreaded.next(ur)
        });
      })
    } catch (error) {
      this.stompClient.disconnect()
    }
  }

  newRequest() {
    return this.requests.asObservable();
  }

  newMessage() {
    return this.unreaded.asObservable();
  }
}
