import { Component } from '@angular/core';
import { TradeService } from '../../services/trade.service';
import { Router } from '@angular/router';
import { AlertService } from '../../services/alert.service';
import { CommonModule } from '@angular/common';
import { environmentsURLs } from '../../utils/environmentsURls';
import Swal from 'sweetalert2';
import { va } from '../../utils/Utils';
import { UserSession } from '../../utils/UserSession';

@Component({
  selector: 'app-traderoom',
  imports: [CommonModule],
  templateUrl: './traderoom.component.html',
  styleUrl: './traderoom.component.css'
})
export class TraderoomComponent extends environmentsURLs {

  you: any
  loaded: boolean = false

  trade: any

  yourcards: any[] = []
  allyourcards: any[] = []
  youroffer: any[] = []
  otheroffer: any[] = []

  constructor(private service: TradeService, private router: Router, private alert: AlertService){
    super()
  }
  joinlistener(id: any) {
    this.service.joinListener(id)
    this.service.getStatus().subscribe((status: any) => {
      if (status != ""){
        if (status == "leave") {
        this.service.disconnect()
        this.router.navigate([`trade`])
      } else {
        status = JSON.parse(status)
        this.loadOther(status)
      }
      }
    })
  }

  ofrecer(card: any){
    if (card.cant == 1){
        this.service.add(card.carta.id, 1, this.trade.id).subscribe({
          next: (data) => {
            if (data != null){
              console.log(data)
              this.loadMy(data)
            } else {
              this.alert.error("Error", "Se ha producido un error al añadir la carta")
            }
          }
        })
    } else {
      Swal.fire({
      icon: "question",
      title: "Oferta",
      text: "¿Cuantos "+card.carta.nombre+" quieres ofrecer?",
      input: "number",
      inputAttributes: {
        min: '0',
        max: card.cant,
        step: '1'
      },
      customClass: {
        popup: "swal-drk btn skew",
        title: "swal-drk",
        confirmButton: "btn but str swal-btn"
      }
    }).then((res) => {
      if (!res.isDismissed){
        if (va(res.value)){
        this.service.add(card.carta.id, Number(res.value), this.trade.id).subscribe({
          next: (data) => {
            if (data != null){
              console.log(data)
              this.loadMy(data)
            } else {
              this.alert.error("Error", "Se ha producido un error al añadir la carta")
            }
          }
        })
      }
      }
    })
    }
  }

  isMe(tradecards: any){
    if (tradecards.usuario.id == UserSession.getId()){
      return true;
    } else {
      return false;
    }
  }

  load(){
    this.youroffer.forEach((card: any) => {
      this.rmv(card)
    })
  }

  rmv(card: any){
    this.yourcards.forEach((c: any, i: any) => {
      if (c.carta.id == card.carta.id){
        c.cant -= card.cant
        if (c.cant <= 0){
          this.yourcards.splice(i,1)
        }
      }
    })
  }

  loadOther(trade: any){
    this.otheroffer = []
    if (trade.cartas != null){
      Array.from(trade.cartas).forEach((pc: any) => {
        if (!this.isMe(pc)){
          this.otheroffer.push(pc)
        }
      })
    }
    this.load()
  }
  
  loadMy(trade: any){
    this.yourcards = [...this.allyourcards]
    this.youroffer = []
    if (trade.cartas != null){
      Array.from(trade.cartas).forEach((pc: any) => {
        if (this.isMe(pc)){
          this.youroffer.push(pc)
        }
      })
    }
    this.load()
  }

  ngOnInit(): void {
    this.joinlistener(sessionStorage.getItem("trade"))
    this.service.getTrade(sessionStorage.getItem("trade")).subscribe({
      next: (data) =>{
        if (data != null){
          this.trade = data
          console.log(data)
          this.loadMy(data)
          this.loadOther(data)
          this.service.findAllPlayerCards().subscribe({
            next: (resp) => {
              this.allyourcards = resp
              this.yourcards = [...this.allyourcards]
              this.loaded = true
            }
          })
        } else {
          this.alert.error("Error", "Ha habido un problema recuperando el intercambio").then(()=>{
            this.router.navigate(['trade'])
          })
        }
      }
    })
  }
  other(){
    
  }
}
