import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { environmentsURLs } from '../../utils/environmentsURls';
import { GameService } from '../../services/game.service';
import { UserSession } from '../../utils/UserSession';

@Component({
  selector: 'app-prev-games',
  imports: [CommonModule],
  templateUrl: './prev-games.component.html',
  styleUrl: './prev-games.component.css'
})
export class PrevGamesComponent extends environmentsURLs implements OnInit {

  loaded: boolean = false

  games: any

  constructor(private service: GameService){
    super()
  }

  won(game: any) : boolean{
    return game.winner == UserSession.getId()
  }
  getWinner(game: any) : string{
    let status = game.status + ""
    let winner = status.split(":")[1]
    winner = winner.replace(" ", "")
    return winner
  }

  ngOnInit(): void {
    this.service.getPastGames().subscribe({
      next: (data) => {
        this.games = data
        this.loaded = true
      }
    })
  }

}
