import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-nav',
  imports: [],
  templateUrl: './nav.component.html',
  styleUrl: './nav.component.css'
})
export class NavComponent implements OnInit {

  constructor(protected router: Router) { }

  cooldown = false;

  registanim = [
    { transform: 'translate(-5%, 100%)' },
    { transform: 'translate(0%, 0%)' },
  ]

  loginanim = [
    { transform: 'translate(0%, 0%)' },
    { transform: 'translate(-5%, 100%)' },
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
      if (this.router.url == "/login" || this.router.url == "/register") {
        const navcont = document.getElementById("nav-login") as HTMLElement
        navcont.style.display = "flex"
        const nav = document.getElementById('nav-element-login') as HTMLElement
        nav.animate(this.router.url == "/login" ? this.registanim : this.loginanim, this.start)
      } else {

      }
    }, 1);
  }

  togglelogin() {
    if (!this.cooldown) {
      this.cooldown = true
      const nav = document.getElementById("nav-element-login") as HTMLElement
      if (this.router.url == "/login") {
        this.router.navigate(['register'])
        nav.animate(this.loginanim, this.animoptions)
      } else {
        this.router.navigate(['login'])
        nav.animate(this.registanim, this.animoptions)
      }
      setTimeout(() => {
        this.cooldown = false
      }, 650);
    }
  }
}
