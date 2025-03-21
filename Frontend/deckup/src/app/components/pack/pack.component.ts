import { Component } from '@angular/core';
import { environmentsURLs } from '../../utils/environmentsURls';

@Component({
  selector: 'app-pack',
  imports: [],
  templateUrl: './pack.component.html',
  styleUrl: './pack.component.css'
})
export class PackComponent extends environmentsURLs {

  constructor() {
    super()
  }

  public static showRecivedCards(pack: any, cards: any[]) {

    let cards_left = pack.cant;

    
    const shadow = document.createElement("div")
    shadow.id = "card_open_shadow"
    document.body.appendChild(shadow)

    const pack_div = document.createElement("div")
    pack_div.id = "element_pack"
    pack_div.className = "pack_open"
    const packimg = document.createElement("img")
    packimg.src = `${this.resURL}/Resources/img/packs/${pack.imagen}`
    pack_div.appendChild(packimg)

    document.body.appendChild(pack_div)

    pack_div.addEventListener("click", () => {
      document.body.removeChild(pack_div)
      const nextcard = document.getElementById(`element_card${cards_left}`) as HTMLElement
      nextcard.style.display = "flex"
    })

    cards.forEach(card => {

      const card_div = document.createElement("div")
      card_div.id = `element_card${cards_left}`
      card_div.className = "card_open"
      card_div.style.display = "none"

      const card_face = document.createElement("div")
      const card_img = document.createElement("img")
      card_img.src = `${this.resURL}/Resources/img/cards/${card.imagen}`

      const card_back = document.createElement("div")
      const card_img_back = document.createElement("img")
      card_img_back.src = `${this.resURL}/Resources/img/misc/${card.rarezaDTO.nombre}_back.png`
      card_back.className = "card_back"

      const card_cont = document.createElement("div")

      card_face.appendChild(card_img)
      card_cont.appendChild(card_face)

      card_back.appendChild(card_img_back)
      card_cont.appendChild(card_back)

      card_div.appendChild(card_cont)

      cards_left--;

      document.body.appendChild(card_div)

      card_div.addEventListener("click", () => {
        card_div.style.animation = "bounceOutUp 1s forwards"
        setTimeout(() => {
          cards_left--
          document.body.removeChild(card_div)
          if (cards_left != 0) {
            const nextcard = document.getElementById(`element_card${cards_left}`) as HTMLElement
            nextcard.style.display = "flex"
          } else {
            document.body.removeChild(document.getElementById("card_open_shadow") as HTMLElement)
          }
        }, 1000);
      })
    });

    cards_left = pack.cant;

  }

}
