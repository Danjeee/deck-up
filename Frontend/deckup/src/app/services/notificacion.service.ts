import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { environmentsURLs } from '../utils/environmentsURls';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

@Injectable({
  providedIn: 'root'
})
export class NotificacionService extends environmentsURLs {
  chatURL = `${this.apiURL}/ws`

  private stompClient: any;

  private requests: BehaviorSubject<any[]> = new BehaviorSubject<any[]>([])

  private unreaded: BehaviorSubject<any[]> = new BehaviorSubject<any[]>([])

  constructor(private http: HttpClient) {
    super()
    this.initConectionSocket()
  }
  initConectionSocket() {
    const socket = new SockJS(this.chatURL)
    this.stompClient = Stomp.over(socket)
  }

  joinListener() {
    try {
      this.stompClient.connect({}, () => {
        this.stompClient.subscribe(`/topic/requests`, (messages: any) => {
          const request = JSON.parse(messages.body)
          const currentMessage = this.requests.getValue()
          currentMessage.push(request)
          this.requests.next(currentMessage)
        });
        this.stompClient.subscribe(`/topic/unreaded`, (messages: any) => {
          const ur = JSON.parse(messages.body)
          const currentMessage = this.unreaded.getValue()
          currentMessage.push(ur)
          this.requests.next(currentMessage)
        });
      })
    } catch (error) {
      this.stompClient.disconnect()
    }
  }

  getSolicitudesAmistad() {
    return this.requests.asObservable();
  }

  getMensajes() {
    return this.unreaded.asObservable();
  }
}
