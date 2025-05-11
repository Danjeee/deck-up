import { CommonModule } from '@angular/common';
import { AfterViewInit, Component, OnInit } from '@angular/core';
import { NavigationEnd, NavigationStart, Router, RoutesRecognized } from '@angular/router';
import { User } from '../../utils/User';
import { UserSession } from '../../utils/UserSession';
import { UserService } from '../../services/user.service';
import Swal from 'sweetalert2';
import { AlertService } from '../../services/alert.service';
import { filter, pairwise } from 'rxjs/operators';
import { LoadComponent } from '../load/load.component';
import { environmentsURLs } from '../../utils/environmentsURls';
import { ParticleComponent } from '../particle/particle.component';
import { ColeccionComponent } from "../coleccion/coleccion.component";
import { FriendService } from '../../services/friend.service';
import { NotificacionService } from '../../services/notificacion.service';
import { ChatService } from '../../services/chat.service';

@Component({
  selector: 'app-nav',
  imports: [CommonModule],
  templateUrl: './nav.component.html',
  styleUrl: './nav.component.css'
})
export class NavComponent extends environmentsURLs implements AfterViewInit {


  constructor(protected router: Router, private service: UserService, private alert: AlertService, private friendservice: FriendService, private notifService: NotificacionService, private chatservice: ChatService) {
    super()
  }

  cooldown = false;

  totalnotif: number = 0

  user: User | any = UserSession.getUser()

  open: boolean = false

  openFriendList: boolean = false

  cd: boolean = false

  friendsloaded: boolean = false

  unreadedmsgs: any

  msgsload: boolean = false

  friendsect: string = "a"

  friends: any

  requests: any

  registanim = [
    { transform: 'translate(-10vh, 110vw)' },
    { transform: 'translate(0vh, 0vw)' },
  ]

  loginanim = [
    { transform: 'translate(0vh, 0vw)' },
    { transform: 'translate(-10vh, 110vw)' },
  ]

  closeanim = [
    { transform: 'translate(0vh, -50%)' },
    { transform: 'translate(100vh, -50%)' },
  ]

  shadowvanish = [
    { opacity: '.7' },
    { opacity: '0' },
  ]

  animoptions = {
    duration: 400,
    fill: 'forwards',
    easing: 'linear'
  } as KeyframeAnimationOptions

  start = {
    duration: 0,
    fill: 'forwards'
  } as KeyframeAnimationOptions

  close = {
    duration: 400,
    easing: 'linear'
  } as KeyframeAnimationOptions

  loadmsgs(){
    this.chatservice.loadMsgs().subscribe({
      next: (data) => {
        this.unreadedmsgs = data
        this.msgsload = true
        if (this.router.url.includes("/chat")){
          this.unreadedmsgs = Array.from(this.unreadedmsgs).splice(0,Array.from(this.unreadedmsgs).length-2)
      }
      }
    })
  }

  getCantNotif(friendid: number){
    let cant = 0
    Array.from(this.unreadedmsgs).forEach((e:any) => {
      if (e.usuarioId == friendid){
        cant++
      }
    })
    return cant
  }

  popupMsg(msg: string){
    const cont = document.getElementById("notifications") as HTMLElement
    const notif = document.createElement("div")
    notif.id = "notif"+this.totalnotif
    this.totalnotif++
    notif.className = "btn skew bg-s str"
    notif.innerHTML = msg
    cont.appendChild(notif)
    notif.animate([
      {opacity: 1},
      {opacity: 0}
    ],
    {
      duration: 2000,
      easing: 'linear',
    })
    setTimeout(() => {
      cont.removeChild(notif)
    }, 2001);
  }

  listenerNotif() {

    this.notifService.joinListener()

    this.notifService.getnotifications().subscribe((messages: any) => {
      this.loadmsgs()
      this.loadfriends()
      if (messages != ""){
        if (!this.router.url.includes("/chat") && !this.router.url.includes("/game")){
          this.popupMsg(messages)
        }
      }
    })
  }

  loadfriends() {
    this.friendservice.getFriendList().subscribe({
      next: (data) => {
        this.friends = data.friends
        this.requests = data.pending
        this.friendsloaded = true
      }
    })
  }
  ngAfterViewInit(): void {
    this.listenerNotif()
    this.friendsloaded = false
    this.router.events.subscribe(e => {
      if (e instanceof NavigationEnd) {
        this.user = UserSession.getUser()
        this.loadmsgs()
      }
    })
    setTimeout(() => {
      if (this.router.url == "/login" || this.router.url == "/register" || this.router.url == "/") {
        const nav = document.getElementById('nav-element-login') as HTMLElement
        const cont = document.getElementById('nav-login') as HTMLElement
        cont.style.zIndex = "-1"
        nav.animate(this.router.url == "/login" ? this.registanim : this.loginanim, this.start)
        window.removeEventListener("keydown", e => {
          if (e.key == "Escape" || e.key == "Tab") {
            this.toggle()
          }
        })
      } else {
        window.addEventListener("keydown", e => {
          if (e.key == "Escape" || e.key == "Tab") {
            this.toggle()
          }
        })
      }
    }, 1);
  }

  back() {
    sessionStorage.setItem("back", 'true')
    // this.router.navigate([LoadComponent.prev[LoadComponent.prev.length - 1]])
    this.router.navigate(["home"])
    //this.router.navigate([this.router.lastSuccessfulNavigation?.extractedUrl])
  }

  setfsect(sect: string) {
    this.friendsect = sect
  }

  togglelogin() {
    if (!this.cooldown) {

      this.cooldown = true
      const cont = document.getElementById("nav-login") as HTMLElement
      const nav = document.getElementById("nav-element-login") as HTMLElement
      cont.style.zIndex = '3'
      if (this.router.url == "/login") {
        setTimeout(() => {
          this.router.navigate(['register'])
        }, 200);
        nav.animate(this.loginanim, this.animoptions)
      } else {
        setTimeout(() => {
          this.router.navigate(['login'])
        }, 200);
        nav.animate(this.registanim, this.animoptions)
      }
      setTimeout(() => {

        cont.style.zIndex = '-1'
      }, 400);
      setTimeout(() => {
        this.cooldown = false
      }, 650);
    }
  }

  logout() {
    this.alert.confirm('Cerrar sesión', '¿Estas seguro de cerrar sesión?', () => {
      this.service.logout().subscribe({
        next: (data) => {
          if (data.status == 200) {
            this.alert.success(data.tit, data.msg)
            UserSession.logOut()
            this.router.navigate(["/login"])
          }
        },
      }
      )
    })
  }

  toggle() {
    const mh = document.getElementById("menuHamb") as HTMLElement
    const shadow = document.getElementById("shadow") as HTMLElement
    if (!this.cd) {
      this.cd = true
      if (!this.open) {
        mh.style.display = "flex"
        shadow.style.display = "flex"
      } else {
        mh.animate(this.closeanim, this.close)
        if (!this.openFriendList && this.open) {
          shadow.animate(this.shadowvanish, this.close)
        }
        setTimeout(() => {
          mh.style.display = "none"
          if (!this.openFriendList && !this.open) {
            shadow.style.display = "none"
          }
        }, 390);
      }
      this.open = !this.open
      setTimeout(() => {
        this.cd = false
      }, 650);
    }
  }

  friendtoggle() {
    const fl = document.getElementById("friendlist") as HTMLElement
    const shadow = document.getElementById("shadow") as HTMLElement
    if (!this.cd) {
      this.cd = true
      if (!this.openFriendList) {
        fl.style.display = "flex"
        shadow.style.display = "flex"
      } else {
        fl.animate([
          { transform: 'translate(0vh, -50%)' },
          { transform: 'translate(-100vh, -50%)' },
        ], this.close)
        if (this.openFriendList && !this.open) {
          shadow.animate(this.shadowvanish, this.close)
        }
        setTimeout(() => {
          fl.style.display = "none"
          if (!this.openFriendList && !this.open) {
            shadow.style.display = "none"
          }
        }, 390);
      }
      this.openFriendList = !this.openFriendList
      setTimeout(() => {
        this.cd = false
      }, 650);
    }
  }

  closeAll() {
    if (this.open && !this.openFriendList) {
      this.toggle()
    }
    if (this.openFriendList && !this.open) {
      this.friendtoggle()
    }
    if (this.open && this.openFriendList) {
      this.toggle()
      setTimeout(() => {
        this.friendtoggle()
      }, 650);
    }

  }

  deletefriend(friend: any) {
    this.alert.confirm("¿Estas seguro?", `Vas a eliminar a ${friend.amigo.username} de tu lista de amigos`, () => {
      this.friendservice.deletefriend(friend.amigo.id).subscribe({
        next: (data) => {
          if (data.status == 200) {
            this.loadfriends()
          } else {
            this.alert.error(data.tit, data.msg)
          }
        }
      })
    })
  }

  gotoChat(friend: any) {
    this.router.navigate([`home`])
    setTimeout(() => {
      this.router.navigate([`chat/${friend.amigo.username}`])
      this.friendtoggle()
    }, 1);
  }

  acceptreq(friend: any) {
    this.friendservice.acceptreq(friend.amigo.id).subscribe({
      next: (data) => {
        if (data.status == 200) {
          this.loadfriends()
        } else {
          this.alert.error(data.tit, data.msg)
        }
      }
    })
  }

  declinereq(friend: any) {
    this.friendservice.declinereq(friend.amigo.id).subscribe({
      next: (data) => {
        if (data.status == 200) {
          this.loadfriends()
        } else {
          this.alert.error(data.tit, data.msg)
        }
      }
    })
  }

  sendreq() {
    this.alert.ask("Añadir amigo", "Escribe el nombre de usuario de la persona a la que quieres mandar una solicitud", true, false).then((resp) => {
      const value = resp.value
      if (value) {
        this.friendservice.sendreq(value).subscribe({
          next: (data) => {
            if (data.status != 200) {
              this.alert.error(data.tit, data.msg)
            }
          }
        })
      }
    })
  }

  codeinput() {
    this.alert.ask("Codigo promocional", "Inserta un codigo promocional", true, false).then((resp) => {
      const value = resp.value
      if (value) {
        this.service.claimCode(value).subscribe({
          next: (data) => {
            if (data.status == 200) {
              if (data.cant != 0) {
                for (let i = 0; i < 100; i++) {
                  ParticleComponent.generateGems([47, 47])
                }
                this.alert.pago_recibido(data.cant)
                setTimeout(() => {
                  UserSession.get_paid(data.cant)
                }, 2000);
                setTimeout(() => {
                  const currency = document.getElementById("currency_txt") as HTMLElement
                  currency.innerHTML = UserSession.getUser().currency
                }, 2500);
              }
              if (data.cartas != null) {
                this.alert.carta_recibida(data.cartas[0], data.msg)
                ParticleComponent.getCard(data.cartas[0])
              }
            } else {
              this.alert.error(data.tit, data.msg)
            }
          }
        })
      }
    })
  }
  bannedRoutes(type: string = "default"){
    switch (type){
      case "default" :
        if (this.router.url == "/login"){
          return true
        }
        if (this.router.url == "/register"){
          return true
        }
        if (this.router.url == "/"){
          return true
        }
        if (this.router.url == "/verify"){
          return true
        }
        if (this.router.url.startsWith("/trade/")){
          return true
        }
        return false;
    }
  return false;
  }
}
