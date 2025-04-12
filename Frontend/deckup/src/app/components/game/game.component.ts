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
  mana: any

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
    if (this.isYou(data.player1)) {
      this.mana = data.player1.mana
    } else {
      this.mana = data.player2.mana
    }
    if (data.turno == 3) {
      this.turn3(data)
    }
    //sessionStorage.removeItem("game")
    setTimeout(() => {
      if (this.yourturn) {
        this.animatecards(document.querySelectorAll('.card'), this.mana)
      }
    }, 100);
  }

  animatecards(cards: any, mana: any) {
    Array.from(cards).forEach((cardd: any) => {
      const card: HTMLElement = cardd
      card.className = "card disabled"
      if (this.getCardById(cardd).mana <= mana) {
        const card: HTMLElement = cardd
        card.className = "card"
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
                next: (data) => {
                  if (data.status != 200) {
                    this.service.getGame(sessionStorage.getItem("game")).subscribe({
                      next: (data) => {
                        // console.log(data)
                        this.rendergame(data)
                        this.loaded = true
                      }
                    })
                  }
                }
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
      }
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
      if (game.player1.carta1 != null) { count.push(game.player1.carta1) }
      if (game.player1.carta2 != null) { count.push(game.player1.carta2) }
      if (game.player1.carta3 != null) { count.push(game.player1.carta3) }
      if (game.player1.carta4 != null) { count.push(game.player1.carta4) }
      if (game.player1.carta5 != null) { count.push(game.player1.carta5) }
    } else {
      if (game.player2.carta1 != null) { count.push(game.player2.carta1) }
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

  switchTurn() {
    this.service.switchturn(this.gameStatus.id, (this.isYou(this.gameStatus.player1) ? 1 : 2)).subscribe({
      next: (data) => {
        if (data.status != 200) {
          this.service.getGame(sessionStorage.getItem("game")).subscribe({
            next: (data) => {
              // console.log(data)
              this.rendergame(data)
              this.loaded = true
            }
          })
        }
      }
    })
  }

  getCardById(card: any) {
    let cardret: any
    this.cards.forEach((e: any) => {
      if (card.id == e.id) {
        cardret = e
      }
    });
    return cardret
  }

  fight(linea: any, game: any) {
    //1-5 p1, 6-10 p2
    switch (linea) {
      case 1:
        if (game.l1_1 != null) {
          if (game.l2_1 != null) {
            game.l2_1.vida -= game.l1_1.carta.habilidadDTO.dmg
          } else {
            game.player2.vida -= game.l1_1.carta.habilidadDTO.dmg
          }
        }
        (document.getElementById("l1") as HTMLElement).className = "line selectedline";
        return 6;
      case 2:
        if (game.l1_2 != null) {
          if (game.l2_2 != null) {
            game.l2_2.vida -= game.l1_2.carta.habilidadDTO.dmg
          } else {
            game.player2.vida -= game.l1_2.carta.habilidadDTO.dmg
          }
        }
        (document.getElementById("l2") as HTMLElement).className = "line selectedline";
        (document.getElementById("l1") as HTMLElement).className = "line";
        return 7;
      case 3:
        if (game.l1_3 != null) {
          if (game.l2_3 != null) {
            game.l2_3.vida -= game.l1_3.carta.habilidadDTO.dmg
          } else {
            game.player2.vida -= game.l1_3.carta.habilidadDTO.dmg
          }
        }
        (document.getElementById("l3") as HTMLElement).className = "line selectedline";
        (document.getElementById("l2") as HTMLElement).className = "line";
        return 8;
      case 4:
        if (game.l1_4 != null) {
          if (game.l2_4 != null) {
            game.l2_4.vida -= game.l1_4.carta.habilidadDTO.dmg
          } else {
            game.player2.vida -= game.l1_4.carta.habilidadDTO.dmg
          }
        }
        (document.getElementById("l4") as HTMLElement).className = "line selectedline";
        (document.getElementById("l3") as HTMLElement).className = "line";
        return 9;
      case 5:
        if (game.l1_5 != null) {
          if (game.l2_5 != null) {
            game.l2_5.vida -= game.l1_5.carta.habilidadDTO.dmg
          } else {
            game.player2.vida -= game.l1_5.carta.habilidadDTO.dmg
          }
        }
        (document.getElementById("l5") as HTMLElement).className = "line selectedline";
        (document.getElementById("l4") as HTMLElement).className = "line";
        return 10;
      case 6:
        if (game.l2_1 != null) {
          if (game.l1_1 != null) {
            game.l1_1.vida -= game.l2_1.carta.habilidadDTO.dmg
          } else {
            game.player1.vida -= game.l2_1.carta.habilidadDTO.dmg
          }
        }
        return 2;
      case 7:
        if (game.l2_2 != null) {
          if (game.l1_2 != null) {
            game.l1_2.vida -= game.l2_2.carta.habilidadDTO.dmg
          } else {
            game.player1.vida -= game.l2_2.carta.habilidadDTO.dmg
          }
        }
        return 3;
      case 8:
        if (game.l2_3 != null) {
          if (game.l1_3 != null) {
            game.l1_3.vida -= game.l2_3.carta.habilidadDTO.dmg
          } else {
            game.player1.vida -= game.l2_3.carta.habilidadDTO.dmg
          }
        }
        return 4;
      case 9:
        if (game.l2_4 != null) {
          if (game.l1_4 != null) {
            game.l1_4.vida -= game.l2_4.carta.habilidadDTO.dmg
          } else {
            game.player1.vida -= game.l2_4.carta.habilidadDTO.dmg
          }
        }
        return 5;
      case 10:
        if (game.l2_5 != null) {
          if (game.l1_5 != null) {
            game.l1_5.vida -= game.l2_5.carta.habilidadDTO.dmg
          } else {
            game.player1.vida -= game.l2_5.carta.habilidadDTO.dmg
          }
        }
        (document.getElementById("l5") as HTMLElement).className = "line";
        return -1;
      default:
        return -1;

    }
  }

  fightLoop(linea: any, game: any) {
    if (linea != -1) {
      setTimeout(() => {
        this.gameStatus = game
        linea = this.fight(linea, game)
        this.fightLoop(linea, game)
        console.log("linea")
      }, 500);
    } else {
      setTimeout(() => {
        this.service.switchturn(this.gameStatus.id, (this.isYou(this.gameStatus.player1) ? 1 : 2)).subscribe({
          next: (data) => {
            // this.service.getGame(sessionStorage.getItem("game")).subscribe({
            //   next: (data) => {
            //     // console.log(data)
            //     this.rendergame(data)
            //     this.loaded = true
            //   }
            // })
          }
        })
      }, this.isYou(this.gameStatus.player1) ? 0 : 500);
    }
  }

  turn3(game: any = this.gameStatus) {
    this.fightLoop(1, game)
  }

  ngOnDestroy(): void {
    this.service.disconnect()
  }

}
