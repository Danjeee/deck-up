import { Component, OnInit } from '@angular/core';
import { environmentsURLs } from '../../utils/environmentsURls';

@Component({
  selector: 'app-particle',
  imports: [],
  templateUrl: './particle.component.html',
  styleUrl: './particle.component.css'
})
export class ParticleComponent extends environmentsURLs {

  constructor() {
    super()
  }

  private static mouseCd = false

  private static gemImgURL = ParticleComponent.resURL + "/Resources/img/misc/gema.png"

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
}
