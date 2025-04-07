import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { environmentsURLs } from '../../utils/environmentsURls';
import { ColeccionService } from '../../services/coleccion.service';
import { Router } from '@angular/router';
import { AlertService } from '../../services/alert.service';

@Component({
  selector: 'app-deck-builder',
  imports: [CommonModule],
  templateUrl: './deck-builder.component.html',
  styleUrl: './deck-builder.component.css'
})
export class DeckBuilderComponent extends environmentsURLs implements OnInit, OnDestroy {

  playerCards: any
  playerCards_aux: any[] = []
  modalOpen: boolean = false
  cd: boolean = false
  current: number = 1
  loaded: boolean = false
  title: any
  deck: any[] = [{}, {}, {}, {}, {}, {}, {}, {}]
  constructor(private coleccion: ColeccionService, private router: Router, private alert: AlertService) {
    super()
  }
  ngOnDestroy(): void {
    sessionStorage.removeItem("deck-id")
  }
  ngOnInit(): void {
    this.coleccion.findAllPlayerCards().subscribe({
      next: (data) => {
        this.playerCards_aux = data
      }
    })
    if (sessionStorage.getItem("deck-id") != null){
      this.coleccion.findDeck(sessionStorage.getItem("deck-id")).subscribe({
        next: (data)=>{
          this.title = data.nombre
          this.deck[0] = data.carta1
          this.deck[1] = data.carta2
          this.deck[2] = data.carta3
          this.deck[3] = data.carta4
          this.deck[4] = data.carta5
          this.deck[5] = data.carta6
          this.deck[6] = data.carta7
          this.deck[7] = data.carta8
          this.loaded = true
        }
      })
    } else {
      this.loaded = true
    }
  }
  modalToggle(index: number) {
    
    this.playerCards = [...this.playerCards_aux]
    if (this.modalOpen) {
      if (!this.cd) {
        this.cd = true
        const shdw = document.getElementById("shadow") as HTMLElement
        const modal = document.getElementById("modal") as HTMLElement
        shdw.style.opacity = '0'
        modal.style.scale = '0'
        setTimeout(() => {
          this.cd = false
          this.modalOpen = false
        }, 510);
      }
    } else {
      this.modalOpen = true
      this.current = index
      this.removeAnyUsedCards()
      setTimeout(() => {
        const shdw = document.getElementById("shadow") as HTMLElement
        const modal = document.getElementById("modal") as HTMLElement
        shdw.style.opacity = '.5'
        modal.style.scale = '1'
      }, 1);
    }
  }

  removeAnyUsedCards(): any {
    this.deck.forEach((e:any) => {
      if (e.id != null){
        Array.from(this.playerCards).forEach((card: any) => {
          if (card != null){
            if (e.id == card.carta.id){
              this.deletecard(e.id)
            }
          }
        })
      }
    });
  }
  add(card: any){
    this.deck[this.current] = card
    this.modalToggle(1)
  }
  deletecard(id:number){
    Array.from(this.playerCards).forEach((card: any) => {
      if (card != null){
        if(card.carta.id == id){
          this.playerCards[Array.from(this.playerCards).indexOf(card)] = null
        }
      }
    });
  }

  gback() {
    this.router.navigate(['/coleccion'])
  }

  finishedDeck(): boolean{
    let finish: boolean = true
    this.deck.forEach((e:any) => {
      if (e.id == null){
        finish = false
      }
    })
    return finish
  }

  saveDeck(){
    if (sessionStorage.getItem("deck-id") == null){
      const title = document.getElementById("title") as HTMLInputElement
      this.coleccion.saveDeck(this.deck, title.value).subscribe({
        next: (data) => {
          if (data.status == 200){
            this.alert.success(data.tit, data.msg).then((resp)=>{
              this.router.navigate(['/coleccion'])
            })
          } else {
            this.alert.error(data.tit, data.msg)
          }
        }
      })
    } else {
      const title = document.getElementById("title") as HTMLInputElement
      this.coleccion.saveDeck(this.deck, title.value, sessionStorage.getItem("deck-id")).subscribe({
        next: (data) => {
          if (data.status == 200){
            this.alert.success(data.tit, data.msg).then((resp)=>{
              this.router.navigate(['/coleccion'])
            })
          } else {
            this.alert.error(data.tit, data.msg)
          }
        }
      })
    }
  }
}
