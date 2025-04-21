import { AfterViewInit, Component, HostListener, OnDestroy, OnInit } from '@angular/core';
import { environmentsURLs } from '../../utils/environmentsURls';
import { AlertService } from '../../services/alert.service';
import { Router } from '@angular/router';
import { GameService } from '../../services/game.service';
import { UserSession } from '../../utils/UserSession';
import { CommonModule } from '@angular/common';
import { animate, createDraggable, createSpring, stagger, utils } from 'animejs';
import { ParticleComponent } from '../particle/particle.component';

@Component({
  selector: 'app-game',
  imports: [CommonModule],
  templateUrl: './game.component.html',
  styleUrl: './game.component.css'
})
export class GameComponent extends environmentsURLs implements AfterViewInit, OnDestroy {

  loaded: boolean = false
  gameStatus: any = null
  gameActual: any = null
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
        this.service.getGame(id).subscribe({
          next: (data) => {
            this.gameStatus = data;
            this.rendergame(data)
          }
        })
      }
    })
  }

  ngAfterViewInit(): void {
    this.gameStatus = null
    if (sessionStorage.getItem("game") == null || sessionStorage.getItem("game") == "") {
      this.router.navigate(["/home"])
    } else {
      this.joinlistener(sessionStorage.getItem("game"))
      this.service.getGame(sessionStorage.getItem("game")).subscribe({
        next: (data) => {
          if (data.id == sessionStorage.getItem("game")) {
            this.rendergame(data)
            this.loaded = true
          }
        }
      })
    }
  }

  rendergame(data: any) {
    if (this.gameActual == null) {
      this.gameActual = data
    }
    if (data.status != 'activo') {
      if (String(data.status).startsWith("winner: ")) {
        this.victoria(String(data.status).substring(String(data.status).indexOf(":") + 1))
      }
    }
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
    if (data.status != "activo" && !String(data.status).startsWith("winner:")) {
      this.router.navigate(["/home"])
    }
    this.yourturn = false
    if (data.status == "activo") {
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
      let enemy = document.querySelectorAll(".enemy")[0] as HTMLElement
      if (this.isYou(data.player1)) {
        if (data.player2.vida > this.gameActual.player2.vida) {
          ParticleComponent.animejs_explosion(enemy.getBoundingClientRect().left + enemy.getBoundingClientRect().width / 2, enemy.getBoundingClientRect().top + enemy.getBoundingClientRect().height)
        }
        this.mana = data.player1.mana
      } else {
        this.mana = data.player2.mana
        if (data.player1.vida > this.gameActual.player1.vida) {
          ParticleComponent.animejs_explosion(enemy.getBoundingClientRect().left + enemy.getBoundingClientRect().width / 2, enemy.getBoundingClientRect().top + enemy.getBoundingClientRect().height)
        }
      }
      setTimeout(() => {
        if (this.yourturn) {
          this.animatecards(document.querySelectorAll('.card'), this.mana)
          this.animatespells(document.querySelectorAll('.spell'), this.mana)
          this.animatespells_all(document.querySelectorAll('.spell_all'), this.mana)
          this.animatespells_self(document.querySelectorAll('.self_spell'), this.mana)
        }
        this.renderenemyheals()
        this.gameActual = data
      }, 100);
    }
  }

  renderenemyheals() {
    const allenemies = document.querySelectorAll(".oponent")
    allenemies.forEach((e: any) => {
      const enemy: HTMLElement = e.parentElement
      const line: HTMLElement = enemy.parentElement as HTMLElement;
      console.log(e)
      if (!this.isYou(this.gameStatus.player1)) {
        switch (line.id) {
          case "l1":
            if (this.gameStatus.l1_1 != null && this.gameActual.l1_1 != null) {
              if (this.gameStatus.l1_1.vida > this.gameActual.l1_1.vida) {
                ParticleComponent.animejs_explosion(e.getBoundingClientRect().width / 2 + e.getBoundingClientRect().left, e.getBoundingClientRect().height + e.getBoundingClientRect().top)
              }
            }
            break;
          case "l2":
            if (this.gameStatus.l1_2 != null && this.gameActual.l1_2 != null) {
              if (this.gameStatus.l1_2.vida > this.gameActual.l1_2.vida) {
                ParticleComponent.animejs_explosion(e.getBoundingClientRect().width / 2 + e.getBoundingClientRect().left, e.getBoundingClientRect().height + e.getBoundingClientRect().top)
              }
            }
            break;
          case "l3":
            if (this.gameStatus.l1_3 != null && this.gameActual.l1_3 != null) {
              if (this.gameStatus.l1_3.vida > this.gameActual.l1_3.vida) {
                ParticleComponent.animejs_explosion(e.getBoundingClientRect().width / 2 + e.getBoundingClientRect().left, e.getBoundingClientRect().height + e.getBoundingClientRect().top)
              }
            }
            break;
          case "l4":
            if (this.gameStatus.l1_4 != null && this.gameActual.l1_4 != null) {
              if (this.gameStatus.l1_4.vida > this.gameActual.l1_4.vida) {
                ParticleComponent.animejs_explosion(e.getBoundingClientRect().width / 2 + e.getBoundingClientRect().left, e.getBoundingClientRect().height + e.getBoundingClientRect().top)
              }
            }
            break;
          case "l5":
            if (this.gameStatus.l1_5 != null && this.gameActual.l1_5 != null) {
              if (this.gameStatus.l1_5.vida > this.gameActual.l1_5.vida) {
                ParticleComponent.animejs_explosion(e.getBoundingClientRect().width / 2 + e.getBoundingClientRect().left, e.getBoundingClientRect().height + e.getBoundingClientRect().top)
              }
            }
            break;
        }
      } else {
        switch (line.id) {
          case "l1":
            if (this.gameStatus.l2_1 != null && this.gameActual.l2_1 != null) {
              if (this.gameStatus.l2_1.vida > this.gameActual.l2_1.vida) {
                ParticleComponent.animejs_explosion(e.getBoundingClientRect().width / 2 + e.getBoundingClientRect().left, e.getBoundingClientRect().height + e.getBoundingClientRect().top)
              }
            }
            break;
          case "l2":
            if (this.gameStatus.l2_2 != null && this.gameActual.l2_2 != null) {
              if (this.gameStatus.l2_2.vida > this.gameActual.l2_2.vida) {
                ParticleComponent.animejs_explosion(e.getBoundingClientRect().width / 2 + e.getBoundingClientRect().left, e.getBoundingClientRect().height + e.getBoundingClientRect().top)
              }
            }
            break;
          case "l3":
            if (this.gameStatus.l2_3 != null && this.gameActual.l2_3 != null) {
              if (this.gameStatus.l2_3.vida > this.gameActual.l2_3.vida) {
                ParticleComponent.animejs_explosion(e.getBoundingClientRect().width / 2 + e.getBoundingClientRect().left, e.getBoundingClientRect().height + e.getBoundingClientRect().top)
              }
            }
            break;
          case "l4":
            if (this.gameStatus.l2_4 != null && this.gameActual.l2_4 != null) {
              if (this.gameStatus.l2_4.vida > this.gameActual.l2_4.vida) {
                ParticleComponent.animejs_explosion(e.getBoundingClientRect().width / 2 + e.getBoundingClientRect().left, e.getBoundingClientRect().height + e.getBoundingClientRect().top)
              }
            }
            break;
          case "l5":
            if (this.gameStatus.l2_5 != null && this.gameActual.l2_5 != null) {
              if (this.gameStatus.l2_5.vida > this.gameActual.l2_5.vida) {
                ParticleComponent.animejs_explosion(e.getBoundingClientRect().width / 2 + e.getBoundingClientRect().left, e.getBoundingClientRect().height + e.getBoundingClientRect().top)
              }
            }
            break;
        }
      }
    });
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

  animatespells(cards: any, mana: any) {
    Array.from(cards).forEach((cardd: any) => {
      const card: HTMLElement = cardd
      card.className = "spell disabled"
      if (this.getCardById(cardd).mana <= mana) {
        const card: HTMLElement = cardd
        card.className = "spell"
        const targets = document.querySelectorAll(".oponent");
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
              // this.removeghost(currentTarget, ghost.id)
              // this.service.put(currentTarget.id, card.id, this.gameStatus.id, (this.isYou(this.gameStatus.player1) ? 1 : 2)).subscribe({
              //   next: (data) => {
              //     if (data.status != 200) {
              //       this.service.getGame(sessionStorage.getItem("game")).subscribe({
              //         next: (data) => {
              //           // console.log(data)
              //           this.rendergame(data)
              //           this.loaded = true
              //         }
              //       })
              //     }
              //   }
              // })
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

  animatespells_all(cards: any, mana: any) {
    Array.from(cards).forEach((cardd: any) => {
      const card: HTMLElement = cardd
      card.className = "spell_all disabled"
      if (this.getCardById(cardd).mana <= mana) {
        const card: HTMLElement = cardd
        card.className = "spell_all"
        const targets = document.querySelectorAll(".oponent_a");
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
              // this.removeghost(currentTarget, ghost.id)
              // this.service.put(currentTarget.id, card.id, this.gameStatus.id, (this.isYou(this.gameStatus.player1) ? 1 : 2)).subscribe({
              //   next: (data) => {
              //     if (data.status != 200) {
              //       this.service.getGame(sessionStorage.getItem("game")).subscribe({
              //         next: (data) => {
              //           // console.log(data)
              //           this.rendergame(data)
              //           this.loaded = true
              //         }
              //       })
              //     }
              //   }
              // })
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

  animatespells_self(cards: any, mana: any) {
    Array.from(cards).forEach((cardd: any) => {
      const card: HTMLElement = cardd
      card.className = "spell_self disabled"
      if (this.getCardById(cardd).mana <= mana) {
        const card: HTMLElement = cardd
        card.className = "spell_self"
        const targets = document.querySelectorAll(".yourself");
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
              ParticleComponent.animejs_explosion(currentTarget.getBoundingClientRect().left + currentTarget.getBoundingClientRect().width / 2, currentTarget.getBoundingClientRect().top + currentTarget.getBoundingClientRect().height)
              this.removeghost(currentTarget, ghost.id)
              this.service.selfspell(currentTarget.parentElement.id, card.id, this.gameStatus.id, (this.isYou(this.gameStatus.player1) ? 1 : 2)).subscribe({
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
            this.isYou(this.gameStatus.player1) ? this.shoot("oponent_c", 'l1') : this.shoot("me_c", 'l1');
            setTimeout(() => {
              game.l2_1.vida -= game.l1_1.carta.habilidadDTO.dmg
            }, 500);
          } else {
            this.isYou(this.gameStatus.player1) ? this.shoot("oponent", 'l1') : this.shoot("me", 'l1');
            setTimeout(() => {
              game.player2.vida -= game.l1_1.carta.habilidadDTO.dmg
            }, 500);
          }
        }
        (document.getElementById("l1") as HTMLElement).className = "line selectedline";
        return 6;
      case 2:
        if (game.l1_2 != null) {
          if (game.l2_2 != null) {
            this.isYou(this.gameStatus.player1) ? this.shoot("oponent_c", 'l2') : this.shoot("me_c", 'l2');
            setTimeout(() => {
              game.l2_2.vida -= game.l1_2.carta.habilidadDTO.dmg
            }, 500);
          } else {
            this.isYou(this.gameStatus.player1) ? this.shoot("oponent", 'l2') : this.shoot("me", 'l2');
            setTimeout(() => {
              game.player2.vida -= game.l1_2.carta.habilidadDTO.dmg
            }, 500);
          }
        }
        (document.getElementById("l2") as HTMLElement).className = "line selectedline";
        (document.getElementById("l1") as HTMLElement).className = "line";
        return 7;
      case 3:
        if (game.l1_3 != null) {
          if (game.l2_3 != null) {
            this.isYou(this.gameStatus.player1) ? this.shoot("oponent_c", 'l3') : this.shoot("me_c", 'l3');
            setTimeout(() => {
              game.l2_3.vida -= game.l1_3.carta.habilidadDTO.dmg
            }, 500);
          } else {
            this.isYou(this.gameStatus.player1) ? this.shoot("oponent", 'l3') : this.shoot("me", 'l3');
            setTimeout(() => {
              game.player2.vida -= game.l1_3.carta.habilidadDTO.dmg
            }, 500);
          }
        }
        (document.getElementById("l3") as HTMLElement).className = "line selectedline";
        (document.getElementById("l2") as HTMLElement).className = "line";
        return 8;
      case 4:
        if (game.l1_4 != null) {
          if (game.l2_4 != null) {
            this.isYou(this.gameStatus.player1) ? this.shoot("oponent_c", 'l4') : this.shoot("me_c", 'l4');
            setTimeout(() => {
              game.l2_4.vida -= game.l1_4.carta.habilidadDTO.dmg
            }, 500);
          } else {
            this.isYou(this.gameStatus.player1) ? this.shoot("oponent", 'l4') : this.shoot("me", 'l4');
            setTimeout(() => {
              game.player2.vida -= game.l1_4.carta.habilidadDTO.dmg
            }, 500);
          }
        }
        (document.getElementById("l4") as HTMLElement).className = "line selectedline";
        (document.getElementById("l3") as HTMLElement).className = "line";
        return 9;
      case 5:
        if (game.l1_5 != null) {
          if (game.l2_5 != null) {
            this.isYou(this.gameStatus.player1) ? this.shoot("oponent_c", 'l5') : this.shoot("me_c", 'l5');
            setTimeout(() => {
              game.l2_5.vida -= game.l1_5.carta.habilidadDTO.dmg
            }, 500);
          } else {
            this.isYou(this.gameStatus.player1) ? this.shoot("oponent", 'l5') : this.shoot("me", 'l5');
            setTimeout(() => {
              game.player2.vida -= game.l1_5.carta.habilidadDTO.dmg
            }, 500);
          }
        }
        (document.getElementById("l5") as HTMLElement).className = "line selectedline";
        (document.getElementById("l4") as HTMLElement).className = "line";
        return 10;
      case 6:
        if (game.l2_1 != null) {
          if (game.l1_1 != null) {
            setTimeout(() => {
              game.l1_1.vida -= game.l2_1.carta.habilidadDTO.dmg
            }, 500);
            this.isYou(this.gameStatus.player1) ? this.shoot("me_c", 'l1') : this.shoot("oponent_c", 'l1');
          } else {
            this.isYou(this.gameStatus.player1) ? this.shoot("me", 'l1') : this.shoot("oponent", 'l1');
            setTimeout(() => {
              game.player1.vida -= game.l2_1.carta.habilidadDTO.dmg
            }, 500);
          }
        }
        return 2;
      case 7:
        if (game.l2_2 != null) {
          if (game.l1_2 != null) {
            this.isYou(this.gameStatus.player1) ? this.shoot("me_c", 'l2') : this.shoot("oponent_c", 'l2');
            setTimeout(() => {
              game.l1_2.vida -= game.l2_2.carta.habilidadDTO.dmg
            }, 500);
          } else {
            this.isYou(this.gameStatus.player1) ? this.shoot("me", 'l2') : this.shoot("oponent", 'l2');
            setTimeout(() => {
              game.player1.vida -= game.l2_2.carta.habilidadDTO.dmg
            }, 500);
          }
        }
        return 3;
      case 8:
        if (game.l2_3 != null) {
          if (game.l1_3 != null) {
            this.isYou(this.gameStatus.player1) ? this.shoot("me_c", 'l3') : this.shoot("oponent_c", 'l3');
            setTimeout(() => {
              game.l1_3.vida -= game.l2_3.carta.habilidadDTO.dmg
            }, 500);
          } else {
            this.isYou(this.gameStatus.player1) ? this.shoot("me", 'l3') : this.shoot("oponent", 'l3');
            setTimeout(() => {
              game.player1.vida -= game.l2_3.carta.habilidadDTO.dmg
            }, 500);
          }
        }
        return 4;
      case 9:
        if (game.l2_4 != null) {
          if (game.l1_4 != null) {
            this.isYou(this.gameStatus.player1) ? this.shoot("me_c", 'l4') : this.shoot("oponent_c", 'l4');
            setTimeout(() => {
              game.l1_4.vida -= game.l2_4.carta.habilidadDTO.dmg
            }, 500);
          } else {
            this.isYou(this.gameStatus.player1) ? this.shoot("me", 'l4') : this.shoot("oponent", 'l4');
            setTimeout(() => {
              game.player1.vida -= game.l2_4.carta.habilidadDTO.dmg
            }, 500);
          }
        }
        return 5;
      case 10:
        if (game.l2_5 != null) {
          if (game.l1_5 != null) {
            this.isYou(this.gameStatus.player1) ? this.shoot("me_c", 'l5') : this.shoot("oponent_c", 'l5');
            setTimeout(() => {
              game.l1_5.vida -= game.l2_5.carta.habilidadDTO.dmg
            }, 500);
          } else {
            this.isYou(this.gameStatus.player1) ? this.shoot("me", 'l5') : this.shoot("oponent", 'l5');
            setTimeout(() => {
              game.player1.vida -= game.l2_5.carta.habilidadDTO.dmg
            }, 500);
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
  shoot(where: any, line: any) {
    const me = document.getElementsByClassName("you")[0] as HTMLElement
    const enemy = document.getElementsByClassName("enemy")[0] as HTMLElement
    const linea = document.getElementById(line) as HTMLElement
    const shoot = document.createElement("div")
    const id = (Math.round(Math.random() * 99999)) + "shot"
    shoot.id = id
    shoot.className = id
    shoot.style.width = linea.getBoundingClientRect().width / 4 + "px"
    shoot.style.height = linea.getBoundingClientRect().height + "px"
    shoot.style.position = "fixed"
    //shoot.style.display = "flex"
    // shoot.style.alignItems = "center"
    //shoot.style.flexDirection = "column"
    // shoot.style.overflow = "hidden"
    shoot.style.zIndex = "2"
    document.body.appendChild(shoot)
    let x
    let y
    const animated: any[] = []
    for (let i = 0; i < Math.floor((shoot.getBoundingClientRect().height / shoot.getBoundingClientRect().width)); i++) {
      const part = document.createElement("div")
      part.className = "exp_part"
      const tmn = shoot.getBoundingClientRect().width
      part.style.width = tmn + "px"
      part.style.height = tmn + "px"
      part.style.backdropFilter = "blur(10px);"
      part.style.backgroundColor = "rgba(0,0,0,.5)"
      shoot.appendChild(part)
      animated.push(part)
    }
    switch (where) {
      case 'me_c':
        y = linea.getBoundingClientRect().top
        // finalY = linea.getBoundingClientRect().bottom/1.5
        x = linea.getBoundingClientRect().left + linea.getBoundingClientRect().width / 2 - shoot.getBoundingClientRect().width / 2
        shoot.style.top = y + "px"
        shoot.style.left = x + "px"
        this.animateShot(animated, shoot, false)
        setTimeout(() => {
          ParticleComponent.animejs_explosion(linea.getBoundingClientRect().left + linea.getBoundingClientRect().width / 2, linea.getBoundingClientRect().bottom)
        }, 450);
        // animate(shoot, { y: finalY, duration: 450 }).then(()=>{
        //   document.body.removeChild(shoot)
        //   
        // })
        break;
      case 'oponent_c':
        y = linea.getBoundingClientRect().top
        // finalY = -linea.getBoundingClientRect().top*3
        x = linea.getBoundingClientRect().left + linea.getBoundingClientRect().width / 2 - shoot.getBoundingClientRect().width / 2
        shoot.style.top = y + "px"
        shoot.style.left = x + "px"
        this.animateShot(animated, shoot, true)
        setTimeout(() => {
          ParticleComponent.animejs_explosion(linea.getBoundingClientRect().left + linea.getBoundingClientRect().width / 2, linea.getBoundingClientRect().top * 1.5)
        }, 450);
        // animate(shoot, { y: finalY, duration: 450 }).then(()=>{
        //   document.body.removeChild(shoot)
        //   ParticleComponent.animejs_explosion(linea.getBoundingClientRect().left + linea.getBoundingClientRect().width / 2 , linea.getBoundingClientRect().top*1.5)
        // })
        break;
      case 'oponent':
        y = linea.getBoundingClientRect().top - 50
        x = linea.getBoundingClientRect().left + linea.getBoundingClientRect().width / 2 - shoot.getBoundingClientRect().width / 2
        shoot.style.top = y + "px"
        shoot.style.left = x + "px"
        this.animateShot(animated, shoot, true)
        setTimeout(() => {
          enemy.style.animation = "hit .3s linear"
          enemy.style.backgroundColor = "#d33"
          setTimeout(() => {
            enemy.style.animation = ""
            enemy.style.backgroundColor = "#fff"
          }, 301);
          ParticleComponent.animejs_explosion(linea.getBoundingClientRect().left + linea.getBoundingClientRect().width / 2, linea.getBoundingClientRect().top * 1.5 - 50)
        }, 450);
        break;
      case 'me':
        y = linea.getBoundingClientRect().top + 50
        x = linea.getBoundingClientRect().left + linea.getBoundingClientRect().width / 2 - shoot.getBoundingClientRect().width / 2
        shoot.style.top = y + "px"
        shoot.style.left = x + "px"
        this.animateShot(animated, shoot, false)
        setTimeout(() => {
          me.style.animation = "hit .3s linear"
          me.style.backgroundColor = "#d33"
          setTimeout(() => {
            me.style.animation = ""
            me.style.backgroundColor = "#fff"
          }, 301);
          ParticleComponent.animejs_explosion(linea.getBoundingClientRect().left + linea.getBoundingClientRect().width / 2, linea.getBoundingClientRect().bottom + 50)
        }, 450);

        break;
    }
  }

  animateShot($squares: any, cont: any, your: boolean) {
    animate($squares, {
      scale: [
        { to: [0, 1.25] },
        { to: 0 }
      ],
      boxShadow: [
        { to: '0 0 1rem 0 currentColor' },
        { to: '0 0 0rem 0 currentColor' }
      ],
      delay: stagger(30, {
        from: (!your ? 'first' : 'last')
      }),
    }).then(() => {
      document.body.removeChild(cont)
    })
  }

  victoria(player: string) {
    sessionStorage.removeItem("game")
    this.service.disconnect()
    const v = document.getElementById('v') as HTMLElement
    const title = document.getElementById('victory') as HTMLElement
    title.innerHTML = "Ganador: " + player
    v.style.display = "flex"

    // Animar título
    animate(title, {
      opacity: [0, 1],
      scale: [0.8, 1],
      easing: 'easeOutExpo',
      duration: 1000,
      delay: 300
    })

    const total: any[] = []
    for (let i = 0; i < 100; i++) {
      const sq = document.createElement("div")
      sq.style.width = "10dvw"
      sq.style.height = "10dvh"
      v.appendChild(sq)
      total.push(sq)
    }
    animate(total, {
      scale: [
        { to: [0, 1.25] },
        { to: 0 }
      ],
      boxShadow: [
        { to: '0 0 1rem 0 currentColor' },
        { to: '0 0 0rem 0 currentColor' }
      ],
      delay: stagger(500, {
        grid: [10, 10],
        from: 'center'
      }),
    }).then(() => {
      this.router.navigate(['/home'])
    })
  }

  // @HostListener('window:beforeunload', ['$event'])
  // instantlose($event: BeforeUnloadEvent){
  //   sessionStorage.removeItem("game")
  //   this.service.lose(this.gameStatus.id).subscribe({
  //     next: (data) => {
  //       console.log(data)
  //     }
  //   })
  // }

  ngOnDestroy(): void {
    this.service.disconnect()
    if (this.router.url != "/game") {
      // this.service.lose(this.gameStatus.id).subscribe({
      //   next: (data) => {
      //     console.log(data)
      //   }
      // })
    }
  }

}
