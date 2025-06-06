import { AfterViewChecked, AfterViewInit, Component, OnInit } from '@angular/core';
import { environmentsURLs } from '../../utils/environmentsURls';
import { Router } from '@angular/router';
import { $$, css, DOM, generateHeader, popUpOnScroll, shuffleArray } from '../../utils/Utils';
import { CommonModule } from '@angular/common';
import { UserSession } from '../../utils/UserSession';

@Component({
  selector: 'app-default',
  imports: [CommonModule],
  templateUrl: './default.component.html',
  styleUrl: './default.component.css'
})
export class DefaultComponent extends environmentsURLs implements OnInit {

  shuffled: { t: string, c: string }[] = []

  blocks: { t: string, c: string }[] = [
    {
      t: "¿Qué es?",
      c: "Deck Up es una aplicación en la que puedes descubrir cartas increibles, conocer gente y luchar por ser el mejor o tener las mejores cartas"
    },
    {
      t: "¿Cómo funciona?",
      c: "Deck Up es sencillo, fácil y divertido, solo tienes que coleccionar, luchar, intercambiar, cualquier cosa vale"
    },
    {
      t: "Colecciona cartas",
      c: "Puedes coleccionar hasta 16 cartas diferentes repartidas en 6 rarezas, cada una con habilidades únicas"
    },
    {
      t: "Lucha sin limites",
      c: "Puedes batallar tantas veces como quieras, y en cada batalla ganada obtendrás gemas que podrás usar para progresar, no hay limites"
    }
  ]

  constructor(private router: Router) {
    super()
  }

  generarBloques() {
    this.shuffled = shuffleArray(this.blocks)
    setTimeout(() => {
      this.generarBloquesPt2()
    }, 10);
  }

  goto(url: string){
    this.router.navigate([url])
  }

  generarBloquesPt2(){
    const clases = ["Comun", "Rara", "Epica", "Legendaria", "idk"];
    ($$(".feature") as NodeListOf<HTMLElement>).forEach((e: HTMLElement) => {
      let clase = clases[Math.floor(Math.random() * clases.length)]
      e.className = "feature " + clase
    })
    popUpOnScroll(".feature")
  }

  ngOnInit(): void {
    let but = {}
    if (UserSession.getUser() == "Guest"){
      but = {
        text: "<i class='bi bi-person-fill'></i> Log in",
        action: () => {
          this.router.navigate(["login"])
        },
        class: "btn but bg-s"
      }
    } else {
      but = {
        text: "<i class='bi bi-house-fill'></i> Home",
        action: () => {
          this.router.navigate(["home"])
        },
        class: "btn but bg-s"
      }
    }

    generateHeader({
      height: "12dvh",
      background: "#13253e",
      title: "DeckUp",
      customClass: "str",
      textColor: "#5898d8",
      logo: `${this.resURL}/Resources/img/misc/deckuplogo_png.webp`,
      logoPosition: "left",
      titlePosition: "center"
    }, {
      logoHref: "https://deckup.tecnobyte.com",
      logoTarget: "current",
      headerActiveRoutes: ["/", "tutorial"]
    }, {
      login: but
    })
    document.body.style.overflowY = "auto"

    this.generarBloques()
  }
}
