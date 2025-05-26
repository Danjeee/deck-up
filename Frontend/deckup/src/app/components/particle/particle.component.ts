import { Component, OnInit } from '@angular/core';
import { environmentsURLs } from '../../utils/environmentsURls';
import { animate, stagger } from 'animejs';
import { css } from '../../utils/Utils';
import { Router } from '@angular/router';

@Component({
  selector: 'app-particle',
  imports: [],
  templateUrl: './particle.component.html',
  styleUrl: './particle.component.css'
})
export class ParticleComponent extends environmentsURLs {

  static totalnotif: number = 0

  constructor() {
    super()
  }

  private static isSpawning: boolean = false

  private static mouseCd = false

  private static gemImgURL = ParticleComponent.resURL + "/Resources/img/misc/gema.webp"

  private static totalGems = 0;

  private static explosion(max: number[]) {
    return [
      { transform: 'translate(0vh, 0vw)' },
      { transform: `translate(${max[0]}vh, ${max[1]}vw)` },
    ]
  }

  private static explosion_anim(dur: number) {
    return {
      duration: dur,
      fill: 'forwards',
      easing: 'linear',
    } as KeyframeAnimationOptions
  }

  private static gotoCurrency(max: number[], vanish?: boolean) {
    let op = 0
    if (!vanish) {
      op = 1
    }
    return [
      {
        transform: `translate(${max[0]}vw, ${max[1]}vh)`,
        opacity: 1
      },
      {
        transform: `translate(35vw, -45vh)`,
        opacity: op
      },
    ]
  }

  public static wave(backgroundColor?: string, dur?: number) {
    const wave = document.createElement("div")
    wave.className = "onda"
    wave.style.zIndex = "40"
    wave.style.animation = "expandir " + (dur ? dur : 1.5) + "s ease-out infinite"
    if (backgroundColor) {
      wave.style.backgroundColor = backgroundColor
    }
    document.body.append(wave)
    setTimeout(() => {
      document.body.removeChild(wave)
    }, (dur ? dur * 1050 : 1050));
  }

  public static generateGems(startingPoint: number[]) { // This is not used as a x and y values, instaad is a position top and left so 0,0 is 40,40 (aprox) (since is not transformed)
    const max_exp = [(Math.random() * 50) - 20, (Math.random() * 45) - 20]
    const dur = Math.floor(Math.random() * 5 + 2) * 100
    const id = this.totalGems
    this.totalGems--

    const gem = document.createElement("div")
    gem.id = id + ""
    gem.style.position = "fixed"
    gem.style.top = (startingPoint[0]) + "vh"
    gem.style.left = (startingPoint[1]) + "vw"
    gem.style.width = "50px"
    gem.style.height = "50px"
    const gem_img = document.createElement("img")
    gem_img.style.width = "100%"
    gem_img.src = this.gemImgURL
    gem.appendChild(gem_img)
    document.body.appendChild(gem)
    gem.animate(ParticleComponent.explosion(max_exp), this.explosion_anim(dur))
    setTimeout(() => {
      gem.animate(ParticleComponent.gotoCurrency(max_exp, true), this.explosion_anim(1000))
    }, dur + 1000);
    setTimeout(() => {
      document.body.removeChild(document.getElementById(id + "") as Node)
    }, dur + 2050);

  }
  public static getCard(card_data: any) {
    const max_exp = [(Math.random() * 50) - 20, (Math.random() * 45) - 20]
    const dur = Math.floor(Math.random() * 5 + 2) * 100


    const card = document.createElement("div") as HTMLElement
    card.id = card_data.id + "getcard"
    card.style.position = "fixed"
    card.style.top = "40vh"
    card.style.left = "40vw"
    card.style.width = "75px"
    card.style.height = "112.5px"
    card.style.overflow = "none"
    const card_img = document.createElement("img")
    card_img.style.width = "100%"
    card_img.style.height = "100%"
    card_img.src = this.resURL + "/Resources/img/cards/" + card_data.imagen
    card.appendChild(card_img)
    document.body.appendChild(card)
    card.animate(ParticleComponent.explosion(max_exp), this.explosion_anim(dur))
    setTimeout(() => {
      card.animate(ParticleComponent.gotoCurrency(max_exp, true), this.explosion_anim(1000))
    }, dur + 1000);
    setTimeout(() => {
      document.body.removeChild(document.getElementById(card_data.id + "getcard") as Node)
    }, dur + 2050);
  }

  public static minimalMsg(e: any, msg: String, time?: number, backgroundColor?: string) {
    if (!this.mouseCd) {

      const mouseX = e.clientX;
      const mouseY = e.clientY;

      this.mouseCd = true
      const div = document.createElement("div")
      div.style.width = "0"
      div.style.backgroundColor = backgroundColor ? backgroundColor : "#fff"
      div.style.position = "absolute"
      div.style.overflow = "hidden"
      div.className = "btn skew"
      div.style.transition = "width .5s ease-in-out, opacity .7s ease-in-out"
      div.style.whiteSpace = "nowrap"
      div.innerHTML = `<span style='padding: 10px; display: inline-block; color: #000;'>${msg}</span>`
      div.style.left = `${mouseX}px`;
      div.style.top = `${mouseY - div.offsetHeight - 70}px`;
      document.body.appendChild(div)
      setTimeout(() => {
        div.style.width = '260px';
      }, 100);
      setTimeout(() => {
        this.mouseCd = false
      }, time ? (time * 700) : 700);
      setTimeout(() => {
        div.style.width = '0';
        div.style.opacity = "0";
      }, time ? (time * 1000) : 1000);
      setTimeout(() => {
        document.body.removeChild(div)
      }, time ? (time * 2000) : 1500);
    }
  }

  public static popupMsg(msg: string, dur: any = 2, big: boolean = false) {
    const cont = document.getElementById("notifications") as HTMLElement
    const notif = document.createElement("div")
    notif.id = "notiff" + this.totalnotif
    this.totalnotif++
    notif.className = "btn skew bg-p str popup"
    notif.innerHTML = msg
    if (big) {
      css(notif, {
        fontSize: "30px"
      })
    }
    cont.appendChild(notif)
    notif.animate([
      { opacity: 1 },
      { opacity: 0 }
    ],
      {
        duration: dur * 1000,
        easing: 'linear',
      })
    setTimeout(() => {
      cont.removeChild(notif)
    }, dur * 1000 + 1);
  }


  public static animejs_explosion(x: any, y: any, color: string = "#000000") {
    const cont = document.createElement('div')
    cont.id = Math.floor(Math.random() * 9999) + "expcont"
    cont.style.position = "fixed"

    cont.style.zIndex = "5"
    cont.style.display = "flex"
    cont.style.flexWrap = "wrap"
    cont.style.width = "200px"
    const animated: any[] = []
    for (let i = 0; i < 25; i++) {
      const part = document.createElement("div")
      part.className = "exp_part"
      part.style.width = "40px"
      part.style.height = "40px"
      part.style.backdropFilter = "blur(10px);"
      part.style.backgroundColor = color + "80"
      cont.appendChild(part)
      animated.push(part)
    }
    document.body.appendChild(cont)
    cont.style.left = x - 100 + "px"
    cont.style.top = y - cont.getBoundingClientRect().height / 1.5 + "px"
    this.animateGrid(animated, cont)

  }

  private static animateGrid($squares: any, cont: any) {
    animate($squares, {
      scale: [
        { to: [0, 1.25] },
        { to: 0 }
      ],
      boxShadow: [
        { to: '0 0 1rem 0 currentColor' },
        { to: '0 0 0rem 0 currentColor' }
      ],
      delay: stagger(100, {
        grid: [5, 5],
        from: 'center'
      }),
    }).then(() => {
      document.body.removeChild(cont)
    })
  }

  public static generateDragons(cards: any[], ignore: boolean = false) {
    if (ParticleComponent.isSpawning == false || ignore) {
      ParticleComponent.isSpawning = true
      const card = cards[Math.round(Math.random() * cards.length - 1)]
      let pos = Math.random() * 100
      let posY = Math.random() * 100
      let time = Math.random() * 15
      time += 5
      if (pos > 50) {
        pos = 100
      } else {
        pos = 0
      }
      const div = document.createElement("div")
      div.className = "cardflip"
      css(div, {
        position: "fixed",
        top: posY + "%",
        left: "calc(" + pos + "dvw " + (pos == 0 ? "- 165px" : "") + ")",
        width: "165px",
        height: "250px",
        transition: "left " + time + "s linear",
        zIndex: "-1"
      })
      setTimeout(() => {
        if (pos == 0) {
          css(div, {
            left: "100dvw"
          })
        } else {
          css(div, {
            left: "-165px"
          })
        }
      }, 10);
      const img = document.createElement("img")
      img.src = this.resURL + "/Resources/img/cards/" + card.imagen;
      img.className = "inner"
      const cont = document.createElement("div")
      css(cont, {
        width: "100%",
        height: "100%",
        position: "relative"
      })
      css(img, {
        position: "absolute",
        width: "100%",
        height: "100%",
        objectFit: "cover",
        transform: "rotateY(180deg)",
        backfaceVisibility: "hidden"
      })
      const back = document.createElement("img")
      back.src = this.resURL + "/Resources/img/misc/" + (card.rarezaDTO.nombre == "???" ? "idk" : card.rarezaDTO.nombre) + "_back.webp";
      back.className = "back"
      css(back, {
        position: "absolute",
        width: "100%",
        height: "100%",
        objectFit: "cover",
        backfaceVisibility: "hidden"
      })
      cont.appendChild(img)
      cont.appendChild(back)
      div.appendChild(cont)
      document.body.appendChild(div)
      setTimeout(() => {
        if ((window.location.href + "").includes("/login") || (window.location.href + "").includes("/register")) {
          this.generateDragons(cards, true)
        }
      }, Math.random() * 7000 + 5000);
      setTimeout(() => {
        div.remove()
      }, time * 1000 + 100);
    }
  }
}


