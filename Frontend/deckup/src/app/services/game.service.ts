import { Injectable } from '@angular/core';
import { environmentsURLs } from '../utils/environmentsURls';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';
import { UserSession } from '../utils/UserSession';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

@Injectable({
  providedIn: 'root'
})
export class GameService extends environmentsURLs {

  chatURL = `${this.apiURL}/ws`
  
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
          this.stompClient.subscribe(`/matchmaking/${UserSession.getId()}`, (messages: any) => {
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

    disconnect(){
      this.stompClient.disconnect()
    }
}
