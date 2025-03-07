import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from '../../utils/User';
import { UserSession } from '../../utils/UserSession';

@Component({
  selector: 'app-nav',
  imports: [CommonModule],
  templateUrl: './nav.component.html',
  styleUrl: './nav.component.css'
})
export class NavComponent implements OnInit {

  constructor(protected router: Router) { }

  cooldown = false;

  user: User | any = UserSession.getUser()

  registanim = [
    { transform: 'translate(-10vh, 110vw)' },
    { transform: 'translate(0vh, 0vw)' },
  ]

  loginanim = [
    { transform: 'translate(0vh, 0vw)' },
    { transform: 'translate(-10vh, 110vw)' },
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

  ngOnInit(): void {
    setTimeout(() => {
      this.user == UserSession.getUser() as User
      if (this.router.url == "/login" || this.router.url == "/register") {
        const nav = document.getElementById('nav-element-login') as HTMLElement
        const cont = document.getElementById('nav-login') as HTMLElement
        cont.style.zIndex = "-1"
        nav.animate(this.router.url == "/login" ? this.registanim : this.loginanim, this.start)
      } else {
      }
    }, 1);
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
}
