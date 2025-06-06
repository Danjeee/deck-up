import { Component, OnInit } from '@angular/core';
import { environmentsURLs } from '../../utils/environmentsURls';
import { $$, generateHeader, popUpOnScroll, va } from '../../utils/Utils';
import { UserSession } from '../../utils/UserSession';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-tutorial',
  imports: [CommonModule],
  templateUrl: './tutorial.component.html',
  styleUrl: './tutorial.component.css'
})
export class TutorialComponent extends environmentsURLs implements OnInit {
  pages = [
    this.p(" ", " "),
    this.p("1.-", "Menú"),
    this.p("2.-", "Tienda"),
    this.p("3.-", "Colección y mazos"),
    this.p("4.-", "Intercambios"),
    this.p("5.-", "Partidas"),
    this.p("6.-", "Partidas (Avanzado)")
  ]
  actual = 1;

  next() {
    this.actual++;
    this.reloadObservers()
  }
  prev() {
    this.actual--;
    this.reloadObservers()
  }
  p(id: string, tit: string) {
    return { id: id, tit: tit }
  }
  constructor(private router: Router) {
    super()
  }
  reloadObservers() {
    setTimeout(() => {
      popUpOnScroll(".item")
    }, 1);
  }
  gotoPage(p: string){
    this.actual = Number.parseInt(p.substring(0,1));
  }
  ngOnInit(): void {
    if (!va($$("header"))) {
      let but = {}
      if (UserSession.getUser() == "Guest") {
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

      this.reloadObservers()

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
    }
  }
}
