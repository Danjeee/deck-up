import { Component, OnInit } from '@angular/core';
import { environmentsURLs } from '../../utils/environmentsURls';
import { ColeccionService } from '../../services/coleccion.service';
import { ParticleComponent } from '../particle/particle.component';
import { CommonModule } from '@angular/common';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-coleccion',
  imports: [CommonModule],
  templateUrl: './coleccion.component.html',
  styleUrl: './coleccion.component.css'
})
export class ColeccionComponent extends environmentsURLs implements OnInit {


  allCards: any
  playerCards: any
  section: string = "cards"
  deck: any
  allDecks: any
  filters: string[] = []
  rarezas: any
  loaded: boolean = false
  bigcard: any
  showing: boolean = false
  showcd: boolean = false
  showh: boolean = false
  showart: boolean = false
  showhcd: boolean = false

  constructor(private service: ColeccionService) {
    super()
  }


  ngOnInit(): void {
    forkJoin([
      this.service.findAllCards(),
      this.service.findRarezas(),
      this.service.findAllPlayerCards(),
      this.service.findPlayerDecks()
    ]).subscribe(([data1, data2, data3, data4]) => {
      this.allCards = data1;
      this.rarezas = data2;
      this.playerCards = data3;
      this.deck = data4.current;
      this.allDecks = data4.all;
      this.loaded = true;
    })
  }

  isUnlocked(carta: any): boolean {
    let itis: boolean = false
    Array.from(this.playerCards).forEach((e: any) => {
      if (e.carta.id == carta.id) {
        itis = true
      }
    })
    return itis;
  }

  goto(section: string) {
    this.section = section
  }

  checkFilters(carta: any): boolean {
    let show = true;
    if (!this.filters.includes("unobt")) {
      if (carta.exclusive) {
        show = false;
      }
    }
    if (!this.filters.includes(carta.rareza)) {
      show = false;
    }
    return show
  }

  getCartasFromRareza(rareza: string): any[] {
    if (this.allCards != undefined) {
      let cartas: any[] = []
      let allcards: any[] = this.allCards
      allcards.forEach((e: any) => {
        if (e.rarezaDTO.nombre == rareza) {
          cartas.push(e)
        }
      })
      return cartas
    } else {
      return []
    }
  }
  getCant(card: any): string {
    let cant: string = ""
    if (this.allCards != undefined) {
      let allcards: any[] = this.playerCards
      allcards.forEach((e: any) => {
        if (e.carta.id == card.id) {
          cant = e.cant
        }
      })
    }
    return cant
  }

  notUnlocked(e: any, card: any) {
    ParticleComponent.minimalMsg(e, "No has desbloqueado a " + card.nombre)
  }

  show(card: any) {
    if (!this.showcd) {
      this.showcd = true
      this.bigcard = card
      this.showing = true
    }
  }
  close() {
    const cont = document.getElementById("bigcardcont") as HTMLElement
    cont.style.opacity = "0"
    cont.style.scale = "0"
    setTimeout(() => {
      this.showh = false
      this.showing = false
      this.bigcard = null
      this.showcd = false
    }, 600);
  }
  showArt() {
    this.showart = true
  }
  closeArt() {
    const cont = document.getElementById("art") as HTMLElement
    cont.style.opacity = "0"
    cont.style.scale = "0"
    setTimeout(() => {
      this.showart = false
    }, 600);
  }
  showhab() {
    if (!this.showhcd) {
      this.showhcd = true
      if (this.showh) {
        const hab = document.getElementById("hab") as HTMLElement
        hab.animate(
          [
            { height: '10dvh', opacity: 1 },
            { height: '0px', opacity: 0 }
          ],
          {
            fill: "forwards",
            duration: 500
          }
        )
        console.log("neg")
        setTimeout(() => {
          this.showh = !this.showh
        }, 500);
      } else {
        this.showh = !this.showh
      }
      setTimeout(() => {
        this.showhcd = false
      }, 700);
    }
  }

  mirarAlCursorAlClick3D(sensibilidad: number = 0.02): void {
    const imagen = document.getElementById("art_img") as HTMLInputElement
    let mirandoAlCursor = false;
    let eventListener: ((evento: MouseEvent) => void) | null = null;

    imagen.addEventListener('click', () => {
      mirandoAlCursor = !mirandoAlCursor;

      if (mirandoAlCursor) {
        const eventListener = (evento: MouseEvent) => {
          const rect = imagen.getBoundingClientRect();
          const xCentroImagen = rect.left + rect.width / 2;
          const yCentroImagen = rect.top + rect.height / 2;

          const diferenciaX = evento.clientX - xCentroImagen;
          const diferenciaY = evento.clientY - yCentroImagen;

          // Calcula los ángulos de rotación en 3D
          const anguloX = diferenciaY * sensibilidad; // Rotación en el eje X (vertical)
          const anguloY = -diferenciaX * sensibilidad; // Rotación en el eje Y (horizontal)

          imagen.style.transform = `perspective(500px) rotateX(${-anguloX}deg) rotateY(${-anguloY}deg)`;
        };
        document.body.addEventListener('mousemove', eventListener);
      } else {
        if (eventListener) {
          document.body.removeEventListener('mousemove', eventListener);
          eventListener = null;
        }
        imagen.style.transform = 'perspective(500px) rotateX(0deg) rotateY(0deg)';
      }
    });
  }
}
