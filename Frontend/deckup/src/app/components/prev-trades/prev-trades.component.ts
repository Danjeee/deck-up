import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { environmentsURLs } from '../../utils/environmentsURls';
import { TradeService } from '../../services/trade.service';
import { Localizer } from '../../utils/Utils';

@Component({
  selector: 'app-prev-trades',
  imports: [CommonModule],
  templateUrl: './prev-trades.component.html',
  styleUrl: './prev-trades.component.css'
})
export class PrevTradesComponent extends environmentsURLs implements OnInit {

  trades: any
  loaded: boolean = false
  translator: Localizer = new Localizer()

  constructor(private service: TradeService){
    super()
  }

  ngOnInit(): void {
    this.translator.set({
      finished: "Finalizado",
      cancelado: "Cancelado",
    })
    this.service.getPastTrades().subscribe({
      next: (data) => {
        this.trades = this.getTrades(data)
        this.loaded = true
      }
    })
  }
  calcGems(trade: any, player: number): number{
    if (player == 1){
      return Number(trade.p2curr-trade.p1curr)
    } else {
      return Number(trade.p1curr-trade.p2curr)
    }
  }
  getCards(trade: any, player: number){
    const cards: any[] = []
    Array.from(trade.cartas).forEach((card: any) => {
      if (card.usuario.id == trade["player"+player].id){
        cards.push(card)
      }
    })
    return cards;
  }
  getTrades(data: any){
    const trades: any[] = []
    Array.from(data).forEach((t:any) => {
      if (t.player1 != null && t.player2 != null){
        trades.push(t)
      }
    })
    return trades;
  }

}
