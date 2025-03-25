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

@Component({
  selector: 'app-nav',
  imports: [CommonModule],
  templateUrl: './nav.component.html',
  styleUrl: './nav.component.css'
})
export class NavComponent extends environmentsURLs implements AfterViewInit {


  constructor(protected router: Router, private service: UserService, private alert: AlertService) {
    super()
  }

  cooldown = false;

  user: User | any = UserSession.getUser()

  open: boolean = false

  cd: boolean = false

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


  ngAfterViewInit(): void {
    this.router.events.subscribe(e => {
      if (e instanceof NavigationEnd) {
        this.user = UserSession.getUser()
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
    this.router.navigate([LoadComponent.prev])
    //this.router.navigate([this.router.lastSuccessfulNavigation?.extractedUrl])
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
        shadow.animate(this.shadowvanish, this.close)
        setTimeout(() => {
          mh.style.display = "none"
          shadow.style.display = "none"
        }, 390);
      }
      this.open = !this.open
      setTimeout(() => {
        this.cd = false
      }, 650);
    }
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
              if (data.cartas != null){
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
}
