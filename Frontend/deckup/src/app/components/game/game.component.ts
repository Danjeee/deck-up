import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core';
import { environmentsURLs } from '../../utils/environmentsURls';
import { AlertService } from '../../services/alert.service';
import { Router } from '@angular/router';
import { GameService } from '../../services/game.service';
import { UserSession } from '../../utils/UserSession';
import { CommonModule } from '@angular/common';
import { animate, createDraggable, createSpring } from 'animejs';

@Component({
  selector: 'app-game',
  imports: [CommonModule],
  templateUrl: './game.component.html',
  styleUrl: './game.component.css'
})
export class GameComponent extends environmentsURLs implements AfterViewInit, OnDestroy {

  loaded: boolean = false
  gameStatus: any
  gameActual: any
  cards: any
  oponentcards: any
  yourlines: any
  oponentlines: any
  yourturn: boolean = false

  constructor(private alert: AlertService, private router: Router, private service: GameService) {
    super()
  }

  joinlistener(id: any) {
    this.service.joinListener(id)
    this.loaded = false;
    this.service.getstatus().subscribe((status: any) => {
      if (status != "" && status != null) {
        this.service.getGame(this.gameStatus.id).subscribe({
          next: (data) => {
            this.gameStatus = data;
            this.gameActual = this.gameStatus
            this.rendergame(data)
          }
        })
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
          this.rendergame(data)
          this.loaded = true
        }
      })
    }
  }

  rendergame(data: any) {
    this.gameStatus = data
    this.oponentcards = this.oponentCards(data)
    this.yourlines = this.setLines(data)[0]
    this.oponentlines = this.setLines(data)[1]
    this.cards = this.mycards(data)
    if (data == null) {
      this.router.navigate(["/home"])
    }
    if (data.player1.usuario.id != UserSession.getId() && data.player2.usuario.id != UserSession.getId()) {
      this.router.navigate(["/home"])
    }
    if (data.status != "activo") {
      this.router.navigate(["/home"])
    }
    this.yourturn = false
    if (data.turno == 1 && this.isYou(data.player1)) {
      this.yourturn = true
    }
    if (data.turno == 2 && this.isYou(data.player2)) {
      this.yourturn = true
    }
    //sessionStorage.removeItem("game")
    setTimeout(() => {
      if (this.yourturn) {
        this.animatecards(document.querySelectorAll('.card'))
      }
    }, 100);
  }

  animatecards(cards: any) {
    Array.from(cards).forEach((cardd: any) => {
      const card: HTMLElement = cardd
      const targets = document.querySelectorAll(".able");
      let ghost: any = null;
      let currentTarget: any = null;
      const DRAG_THRESHOLD = 100;
      const draggable = createDraggable(card, {
        dragSpeed: .9,
        onGrab: (e) => {
          card.style.transition = ""
        },
        onDrag: () => {
          const dragRect = card.getBoundingClientRect();

          let closest: any = null;
          let minDist = Infinity;

          targets.forEach((target) => {
            const rect = target.getBoundingClientRect();
            const dx = rect.left + rect.width / 2 - (dragRect.left + dragRect.width / 2);
            const dy = rect.top + rect.height / 2 - (dragRect.top + dragRect.height / 2);
            const dist = Math.hypot(dx, dy);


            if (dist < minDist) {
              minDist = dist;
              closest = target;
            }
          });

          if (minDist < DRAG_THRESHOLD) {
            if (currentTarget !== closest) {
              if (ghost) { this.removeghost(currentTarget, ghost.id) };
              ghost = card.cloneNode(true);
              ghost.classList.add("ghost");
              ghost.id = "ghost"
              ghost.style.opacity = 0;
              closest.appendChild(ghost);
              requestAnimationFrame(() => {
                ghost.style.opacity = 1;
                card.style.opacity = "0";
              });
              currentTarget = closest;
            }
          } else {
            if (ghost) {
              let id = ghost.id
              ghost.style.opacity = 0;
              card.style.opacity = "1";
              this.removeghost(currentTarget, id)
              // setTimeout(() => , 1);
              ghost = null;
              currentTarget = null;
            }
          }
        },
        onRelease: (e) => {
          if (ghost && currentTarget) {
            this.removeghost(currentTarget, ghost.id)
            this.service.put(currentTarget.id, card.id, this.gameStatus.id, (this.isYou(this.gameStatus.player1) ? 1 : 2)).subscribe({
              next: (data) =>{console.log(data)}
            })
            ghost = null;
            currentTarget = null;
          } else if (ghost) {
            ghost.style.opacity = 0;
            setTimeout(() => ghost?.remove(), 300);
            ghost = null;
          }
          setTimeout(() => {
            card.style.transition = "transform .5s ease-in-out"
            setTimeout(() => {
              card.style.transform = ""
              draggable.reset()
            }, 10);
          }, 20);
        }
      });
    });
  }

  removeghost(parent: Node, id: any) {
    parent.removeChild(document.getElementById(id) as Node)
  }

  isYou(player: any): boolean {
    return (player.usuario.id == UserSession.getId())
  }

  oponentCards(game: any = this.gameStatus): number[] {
    let count: number[] = []
    if (this.isYou(this.gameStatus.player2)) {
      if (game.player1.carta1 != null) { count.push(0) }
      if (game.player1.carta2 != null) { count.push(0) }
      if (game.player1.carta3 != null) { count.push(0) }
      if (game.player1.carta4 != null) { count.push(0) }
      if (game.player1.carta5 != null) { count.push(0) }
    } else {
      if (game.player1.carta1 != null) { count.push(0) }
      if (game.player2.carta2 != null) { count.push(0) }
      if (game.player2.carta3 != null) { count.push(0) }
      if (game.player2.carta4 != null) { count.push(0) }
      if (game.player2.carta5 != null) { count.push(0) }
    }
    return count
  }

  mycards(game: any = this.gameStatus): any[] {
    let count: any[] = []
    if (this.isYou(this.gameStatus.player1)) {
      if (game.player1.carta1 != null) { count.push(game.player1.carta1) } // Mete el null por alguna razon
      if (game.player1.carta2 != null) { count.push(game.player1.carta2) }
      if (game.player1.carta3 != null) { count.push(game.player1.carta3) }
      if (game.player1.carta4 != null) { count.push(game.player1.carta4) }
      if (game.player1.carta5 != null) { count.push(game.player1.carta5) }
    } else {
      if (game.player1.carta1 != null) { count.push(game.player2.carta1) }
      if (game.player2.carta2 != null) { count.push(game.player2.carta2) }
      if (game.player2.carta3 != null) { count.push(game.player2.carta3) }
      if (game.player2.carta4 != null) { count.push(game.player2.carta4) }
      if (game.player2.carta5 != null) { count.push(game.player2.carta5) }
    }
    return count
  }

  setLines(game: any = this.gameStatus): any[] {
    let count: any[] = []
    let count2: any[] = []
    if (this.isYou(this.gameStatus.player1)) {
      count.push(game.l1_1)
      count.push(game.l1_2)
      count.push(game.l1_3)
      count.push(game.l1_4)
      count.push(game.l1_5)
      count2.push(game.l2_1)
      count2.push(game.l2_2)
      count2.push(game.l2_3)
      count2.push(game.l2_4)
      count2.push(game.l2_5)
    } else {
      count2.push(game.l1_1)
      count2.push(game.l1_2)
      count2.push(game.l1_3)
      count2.push(game.l1_4)
      count2.push(game.l1_5)
      count.push(game.l2_1)
      count.push(game.l2_2)
      count.push(game.l2_3)
      count.push(game.l2_4)
      count.push(game.l2_5)
    }
    return [count, count2]
  }

  switchTurn(){
    this.service.switchturn(this.gameStatus.id).subscribe({
      next: (data) => {console.log(data)}
    })
  }

  ngOnDestroy(): void {
    this.service.disconnect()
  }

}
