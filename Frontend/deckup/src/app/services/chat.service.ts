import { Injectable } from '@angular/core';
import { environmentsURLs } from '../utils/environmentsURls';
import { Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { UserSession } from '../utils/UserSession';
import { BehaviorSubject, catchError, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ChatService extends environmentsURLs {

  chatURL = `${this.apiURL}/ws`
  
  private stompClient: any;

  private messageSubject: BehaviorSubject<any[]> = new BehaviorSubject<any[]>([])

  constructor(private http: HttpClient) {
    super()
    this.initConectionSocket()
  }

  initConectionSocket(){
    const socket = new SockJS(this.chatURL)
    this.stompClient = Stomp.over(socket)
  }

  joinRoom(roomid: any, prevmsgs: any[]){
    this.messageSubject = new BehaviorSubject<any[]>(prevmsgs);
    
    ((UserSession.getId() as number) < (roomid as number)) ? roomid = (UserSession.getId()+"-"+roomid) : roomid = (roomid+"-"+UserSession.getId())
    try {
      this.stompClient.connect({}, () => {
        this.stompClient.subscribe(`/chat/${roomid}`, (messages: any) => {
          const messageContent = JSON.parse(messages.body)
          const currentMessage = this.messageSubject.getValue()
          currentMessage.push(messageContent)
          this.messageSubject.next(currentMessage)
        })
      })
    } catch (error) {
      this.stompClient.disconnect()
    }
  }

  sendmessage(roomid: any, message: any){
    ((UserSession.getId() as number) < (roomid as number)) ? roomid = (UserSession.getId()+"-"+roomid) : roomid = (roomid+"-"+UserSession.getId())
    try{
      this.stompClient.send(`/app/chat/${roomid}`, {}, JSON.stringify(message))
    } catch {
      this.stompClient.disconnect()
      setTimeout(() => {
        this.joinRoom(roomid, this.messageSubject.value)
        this.stompClient.send(`/app/chat/${roomid}`, {}, JSON.stringify(message))
      }, 100);
    }
  }

  getMessageSubject(){
    return this.messageSubject.asObservable()
  }

  getMessages(userid: any) : Observable<any>{
    return this.http.get(`${this.apiURL}/chat/getMsgs/${UserSession.getId()}-${userid}`).pipe(
      catchError(err => {throw err})
    )
  }
  disconnect(){
    this.stompClient.disconnect(()=>{
      console.log("Desconexión completada")
    })
  }
  read(friendid: any) : Observable<any>{
    const data: FormData = new FormData();
    data.append("user_auth", UserSession.getUser().auth)
    return this.http.post(`${this.apiURL}/chat/read/${friendid}`, data).pipe(
      catchError(err => {throw err})
    )
  }
}
