import { Component, OnDestroy, OnInit } from '@angular/core';
import { ChatService } from '../../services/chat.service';
import { UserSession } from '../../utils/UserSession';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { User } from '../../utils/User';
import { UserService } from '../../services/user.service';
import { Router } from '@angular/router';
import { AlertService } from '../../services/alert.service';
import { environmentsURLs } from '../../utils/environmentsURls';

@Component({
  selector: 'app-chat',
  imports: [FormsModule, CommonModule],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.css'
})
export class ChatComponent extends environmentsURLs implements OnInit, OnDestroy{

  loaded: boolean = false
  user: User = UserSession.getUser()
  friend: any
  messageInput: string = ''
  messageList: any[] = [];

  constructor(private chatService: ChatService, private userService: UserService, private router: Router, private alert: AlertService){
    super()
  }
  ngOnInit(): void {
    const username = this.router.url.split("/")[this.router.url.split("/").length-1]
    this.userService.getForChat(username).subscribe({
      next: (data)=>{
        this.friend = data
        this.chatService.getMessages(this.friend.id).subscribe({
          next: (data) => {
            this.chatService.joinRoom(this.friend.id, data)
            this.listenerMessage()
            this.chatService.read(this.friend.id).subscribe()
            this.loaded = true
          },
          error: (error) => {
            this.alert.error("Error", "Ha habido un error al recuperar los mensajes con "+username).then(()=>{this.router.navigate(['home'])})
          }
        })
      },
      error: (err) => {
        this.alert.error("Error", "Ha habido un error al recuperar a "+username).then(()=>{this.router.navigate(['home'])})
      }
    })
    

  }
  sendmessage(){
    if (this.messageInput.replace(/\s+/g, '') != ''){
      this.chatService.sendmessage(this.friend.id, {
        contenido: this.messageInput,
        usuarioId: UserSession.getId(),
        destinoId: this.friend.id
      })
      this.messageInput = ''
    }
  }

  listenerMessage(){
    this.chatService.getMessageSubject().subscribe((messages: any) => {
      this.messageList = messages
      this.chatService.read(this.friend.id).subscribe()
      setTimeout(() => {
        const msgcont = document.getElementById("msgcont") as HTMLElement
        msgcont.scrollTo({top: msgcont.scrollHeight})
      }, 100);

    })
  }
  ngOnDestroy(): void {
      this.chatService.disconnect()
      setTimeout(() => {
        window.location.reload()
      }, 100);
  }
}
