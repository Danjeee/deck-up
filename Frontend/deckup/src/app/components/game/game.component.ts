import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core';
import { environmentsURLs } from '../../utils/environmentsURls';
import { AlertService } from '../../services/alert.service';
import { Router } from '@angular/router';
import { GameService } from '../../services/game.service';
import { UserSession } from '../../utils/UserSession';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-game',
  imports: [CommonModule],
  templateUrl: './game.component.html',
  styleUrl: './game.component.css'
})
export class GameComponent extends environmentsURLs implements AfterViewInit, OnDestroy {

  loaded:boolean = false
  gameStatus: any
  gameActual: any
  cards: any
  oponentcards: any

  constructor(private alert: AlertService, private router: Router, private service: GameService) {
    super()
  }

  joinlistener(id: any) {
    this.service.joinListener(id)

    this.service.getstatus().subscribe((status: any) => {
      if (status != "" && status != null) {
        this.gameStatus = status;
        this.gameActual = [...this.gameStatus]
      }
    })
  }

  ngAfterViewInit(): void {
    if (sessionStorage.getItem("game") == null || sessionStorage.getItem("game") == "") {
      this.router.navigate(["/home"])
    } else {
      this.joinlistener(sessionStorage.getItem("game"))
      this.service.getGame(sessionStorage.getItem("game")).subscribe({
        next: (data) => {
          console.log(data)
          this.gameStatus = data
          this.oponentcards = this.oponentCards(data)
          if (data.player1.usuario.id == UserSession.getId()){
            this.cards = [data.player1.carta1,data.player1.carta2,data.player1.carta3,data.player1.carta4,data.player1.carta5]
          } else {
            this.cards = [data.player2.carta1,data.player2.carta2,data.player2.carta3,data.player2.carta4,data.player2.carta5]
          }
          if (data == null) {
            this.router.navigate(["/home"])
          }
          if (data.player1.usuario.id != UserSession.getId() && data.player2.usuario.id != UserSession.getId()) {
            this.router.navigate(["/home"])
          }
          if (data.status != "activo"){
            this.router.navigate(["/home"])
          }
          this.loaded = true
          sessionStorage.removeItem("game")
        }
      })
    }
  }

  isYou(player: any):boolean{
    return (player.usuario.id == UserSession.getId())
  }

  oponentCards(game: any = this.gameStatus): number[]{
    let count: number[] = []
    if (this.isYou(this.gameStatus.player1)) {
      if (game.player1.carta1 != null){count.push(0)}
      if (game.player1.carta2 != null){count.push(0)}
      if (game.player1.carta3 != null){count.push(0)}
      if (game.player1.carta4 != null){count.push(0)}
      if (game.player1.carta5 != null){count.push(0)}
    } else {
      if (game.player1.carta1 != null){count.push(0)}
      if (game.player2.carta2 != null){count.push(0)}
      if (game.player2.carta3 != null){count.push(0)}
      if (game.player2.carta4 != null){count.push(0)}
      if (game.player2.carta5 != null){count.push(0)}
    }
    return count
  }

  ngOnDestroy(): void {
      this.service.disconnect()
  }

}
