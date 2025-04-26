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
  haschanged: boolean = false;

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
            if (data.p1_c && data.p2_c == null) {
              setTimeout(() => {
                if (this.isYou(data.player2)) {
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
                }
              }, 100);
              if (data.status != "activo") {
                this.gameStatus = data;
                this.rendergame(data)
              }
            } else {
              status = (status + "").split("/")
              if ((status[0] + "").startsWith("l")){
                if (status[1] != "put"){
                  this.triggerExplosion(document.getElementById(status[0]) as HTMLElement, status[1] == "heal" ? "#198754" : "#d9534f")
                }
              }
              if ((status[0] + "").startsWith("p")){
                const player = document.getElementById(status[0]) as HTMLElement
                this.triggerExplosion(player, status[1] == "heal" ? "#198754" : "#d9534f")
                if (status[1] == "heal"){
                  player.style.backgroundColor = "#198754"
                  setTimeout(() => {
                    player.style.backgroundColor = "#fff"
                  }, 301);
                } else {
                  player.style.backgroundColor = "#d33"
                  setTimeout(() => {
                    player.style.backgroundColor = "#fff"
                  }, 301);
                }
              }
              this.gameStatus = data;
              this.rendergame(data)
            }
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
    let wait = 0
    if (this.gameActual != null) {
      if (data.turno != this.gameActual.turno) {
        this.haschanged = true
        wait = 200
      }
    }
    if (this.gameActual == null || this.haschanged) {
      if (wait != 0) {
        setTimeout(() => {
          this.gameActual = data
        }, wait);
      } else {
        this.gameActual = data
      }
      this.haschanged = false
    }
    if (data.status != 'activo') {
      if (String(data.status).startsWith("winner: ")) {
        this.victoria(String(data.status).substring(String(data.status).indexOf(":") + 1))
      }
    }
    if (this.gameStatus != null) {
      setTimeout(() => {
        this.gameStatus = data
      }, 1000);
    } else {
      this.gameStatus = data
    }
    this.yourlines = this.setLines(data)[0]
    this.oponentlines = this.setLines(data)[1]
    this.oponentcards = this.oponentCards(data)
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
      setTimeout(() => {
        this.renderStatusEffects()
        this.checkStatus()
      }, 300);
      setTimeout(() => {
        if (this.yourturn) {
          this.animatecards(document.querySelectorAll('.card'), this.mana)
          this.animatespells(document.querySelectorAll('.spell'), this.mana)
          this.animatespells_all(document.querySelectorAll('.spell_all'), this.mana)
          this.animatespells_self(document.querySelectorAll('.self_spell'), this.mana)
        }
      }, 100);
    }
  }

  renderenemyheals(data: any) {
    const allenemies = document.querySelectorAll(".oponent");
    allenemies.forEach((e: any) => {
      const enemy = e.parentElement as HTMLElement;
      const line = enemy.parentElement as HTMLElement;

      const isPlayer1 = this.isYou(this.gameStatus.player1);
      const lineNumber = line.id.replace("l", ""); // "l1" → "1"
      const prefix = !isPlayer1 ? "l1" : "l2";
      const key = `${prefix}_${lineNumber}`;
      this.checkHealAndTriggerExplosion(e, key, data);
    });
  }

  private checkHealAndTriggerExplosion(element: HTMLElement, key: string, data: any) {
    const status = this.gameActual[key];
    const current = data[key];
    if (status && current && status.vida < current.vida) {
      this.haschanged = true
      this.triggerExplosion(element);
    }
  }

  renderdmgs(prevdivs: any, lineastocheck: string, data: any) {
    if (!prevdivs) return;

    prevdivs.forEach((e: any) => {
      if (e.id === "player1" || e.id === "player2") return;

      const line = e.parentElement as HTMLElement;
      const lineNumber = line.id.replace("l", ""); // ej. "l1" → "1"
      const playerPrefix = lineastocheck === "lineas1" ? "l1" : "l2";
      const key = `${playerPrefix}_${lineNumber}`;
      this.checkDamageAndTriggerExplosion(e, key, data);
    });
  }

  private checkDamageAndTriggerExplosion(element: HTMLElement, key: string, data: any) {
    const newData = data[key];
    const prevStatus = this.gameActual[key];
    if (!newData) return;
    let shouldExplode
    
    if (prevStatus != null && newData != null) {
      shouldExplode = prevStatus.vida > newData.vida
    }
    if (newData == null && prevStatus != null) {
      shouldExplode = true
    }
    
    if (shouldExplode) {
      this.haschanged = true
      this.triggerExplosion(element);
    }
  }

  private triggerExplosion(element: HTMLElement, color:string = "#000000") {
    const rect = element.getBoundingClientRect();
    ParticleComponent.animejs_explosion(rect.width / 2 + rect.left, rect.height + rect.top, color);
  }

  renderStatusEffects() {
    for (let player = 1; player <= 2; player++) {
      for (let line = 1; line <= 5; line++) {
        let key = `l${player}_${line}`;
        if (this.gameStatus[key] != null) {
          if (this.gameStatus[key].stun != null) {
            if (this.gameStatus[key].stun > 0) {
              (document.getElementById(key) as HTMLElement).classList.add('status');
              (document.getElementById(key) as HTMLElement).classList.add('frozen');
            }
          }
          if (this.gameStatus[key].bleed != null) {
            if (this.gameStatus[key].bleed > 0) {
              (document.getElementById(key) as HTMLElement).classList.add('status');
              (document.getElementById(key) as HTMLElement).classList.add('bleed');
              if (this.gameStatus.turno == 3 && this.gameStatus.p1_c == null){
                 this.triggerExplosion(document.getElementById(key) as HTMLElement)
              }
            }
          }
          if (this.gameStatus[key].poisn != null) {
            if (this.gameStatus[key].poisn > 0) {
              (document.getElementById(key) as HTMLElement).classList.add('status');
              (document.getElementById(key) as HTMLElement).classList.add('poisn');
            }
            if (this.gameStatus.turno == 3 && this.gameStatus.p1_c == null){
              this.triggerExplosion(document.getElementById(key) as HTMLElement)
           }
          }
          if (this.gameStatus[key].burn != null) {
            if (this.gameStatus[key].burn > 0) {
              (document.getElementById(key) as HTMLElement).classList.add('status');
              (document.getElementById(key) as HTMLElement).classList.add('burn');
            }
            if (this.gameStatus.turno == 3 && this.gameStatus.p1_c == null){
              this.triggerExplosion(document.getElementById(key) as HTMLElement)
           }
          }
        }
      }
    }
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
              if (ghost && currentTarget) {
                this.removeghost(currentTarget, ghost.id)
                this.service.spellthrow(currentTarget.parentElement.id, card.id, this.gameStatus.id, (this.isYou(this.gameStatus.player1) ? 1 : 2)).subscribe({
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
              this.removeghost(currentTarget, ghost.id)
              this.service.spellthrow(currentTarget.parentElement.id, card.id, this.gameStatus.id, (this.isYou(this.gameStatus.player1) ? 1 : 2)).subscribe({
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

  checkStatus(){
    for (let player = 1; player <= 2; player++) {
      for (let line = 1; line <= 5; line++) {
        let key = `l${player}_${line}`;
        let count = 0;
        if (this.gameStatus[key] != null) {
          if (this.gameStatus[key].bleed || this.gameStatus[key].bleed == 0 == null) {
            (document.getElementById(key) as HTMLElement).classList.remove('bleed');
            count++;
          }
          if (this.gameStatus[key].poisn == null || this.gameStatus[key].poisn == 0) {
            (document.getElementById(key) as HTMLElement).classList.remove('poisn');
            count++
          }
          if (this.gameStatus[key].burn == null || this.gameStatus[key].burn == 0) {
            (document.getElementById(key) as HTMLElement).classList.remove('burn');
            count++
          }
          if (this.gameStatus[key].stun == null) {
            count++
          }
          if (count == 4){
            (document.getElementById(key) as HTMLElement).classList.remove('status');
          }
        } else {
          (document.getElementById(key) as HTMLElement).classList.remove('status');
          (document.getElementById(key) as HTMLElement).classList.remove('bleed');
          (document.getElementById(key) as HTMLElement).classList.remove('poisn');
          (document.getElementById(key) as HTMLElement).classList.remove('frozen');
          (document.getElementById(key) as HTMLElement).classList.remove('burn');
        }
      }
    }
  }

  checkFreeze(key: string){
        if (this.gameStatus[key] != null) {
          if (this.gameStatus[key].stun != null) {
            if (this.gameStatus[key].stun == 1) {
              (document.getElementById(key) as HTMLElement).classList.remove('frozen');
            }
          }
        }
  }

  fight(linea: any, game: any) {
    //1-5 p1, 6-10 p2
    switch (linea) {
      case 1:
        if (this.canAttack(game.l1_1)) {
          this.performAttack(game.l1_1, game.l2_1, "player2", "l1");
        } else {
          this.checkFreeze("l1_1")
        }
        this.updateLineUI("l1");
        return 6;

      case 2:
        if (this.canAttack(game.l1_2)) {
          this.performAttack(game.l1_2, game.l2_2, "player2", "l2");
        } else {
          this.checkFreeze("l1_2")
        }
        this.updateLineUI("l2", "l1");
        return 7;

      case 3:
        if (this.canAttack(game.l1_3)) {
          this.performAttack(game.l1_3, game.l2_3, "player2", "l3");
        } else {
          this.checkFreeze("l1_3")
        }
        this.updateLineUI("l3", "l2");
        return 8;

      case 4:
        if (this.canAttack(game.l1_4)) {
          this.performAttack(game.l1_4, game.l2_4, "player2", "l4");
        } else {
          this.checkFreeze("l1_4")
        }
        this.updateLineUI("l4", "l3");
        return 9;

      case 5:
        if (this.canAttack(game.l1_5)) {
          this.performAttack(game.l1_5, game.l2_5, "player2", "l5");
        } else {
          this.checkFreeze("l1_5")
        }
        this.updateLineUI("l5", "l4");
        return 10;

      case 6:
        if (this.canAttack(game.l2_1)) {
          this.performAttack(game.l2_1, game.l1_1, "player1", "l1");
        } else {
          this.checkFreeze("l2_1")
        }
        return 2;

      case 7:
        if (this.canAttack(game.l2_2)) {
          this.performAttack(game.l2_2, game.l1_2, "player1", "l2");
        } else {
          this.checkFreeze("l2_2")
        }
        return 3;

      case 8:
        if (this.canAttack(game.l2_3)) {
          this.performAttack(game.l2_3, game.l1_3, "player1", "l3");
        } else {
          this.checkFreeze("l2_3")
        }
        return 4;

      case 9:
        if (this.canAttack(game.l2_4)) {
          this.performAttack(game.l2_4, game.l1_4, "player1", "l4");
        } else {
          this.checkFreeze("l2_4")
        }
        return 5;

      case 10:
        if (this.canAttack(game.l2_5)) {
          this.performAttack(game.l2_5, game.l1_5, "player1", "l5");
        } else {
          this.checkFreeze("l2_5")
        }
        this.updateLineUI("none", "l5"); // deseleccionar
        return -1;

      default:
        return -1;
    }


  }

  private canAttack(unit: any): boolean {
    return unit != null && (unit.stun == null || unit.stun === 0);
  }

  private performAttack(from: any, to: any, playerTarget: string, line: string): void {
    const isPlayer1 = this.isYou(this.gameStatus.player1);
    let shooter
    if (to) {
      if (isPlayer1 && playerTarget == "player1") {
        shooter = "me_c"
      }
      if (isPlayer1 && playerTarget == "player2") {
        shooter = "oponent_c"
      }
      if (!isPlayer1 && playerTarget == "player2") {
        shooter = "me_c"
      }
      if (!isPlayer1 && playerTarget == "player1") {
        shooter = "oponent_c"
      }
    } else {
      if (isPlayer1 && playerTarget == "player1") {
        shooter = "me"
      }
      if (isPlayer1 && playerTarget == "player2") {
        shooter = "oponent"
      }
      if (!isPlayer1 && playerTarget == "player2") {
        shooter = "me"
      }
      if (!isPlayer1 && playerTarget == "player1") {
        shooter = "oponent"
      }
    }
    this.shoot(shooter, line);

    setTimeout(() => {
      if (to) {
        to.vida -= from.carta.habilidadDTO.dmg;
      } else {
        this.gameStatus[playerTarget].vida -= from.carta.habilidadDTO.dmg;
      }
    }, 500);
  }

  private updateLineUI(currentLine: string, previousLine?: string): void {
    if (currentLine != "none") {
      (document.getElementById(currentLine) as HTMLElement).className = "line selectedline";
    }
    if (previousLine) {
      (document.getElementById(previousLine) as HTMLElement).className = "line";
    }
  }


  fightLoop(linea: any, game: any) {
    if (linea != -1) {
      setTimeout(() => {
        this.gameStatus = game
        linea = this.fight(linea, game)
        this.fightLoop(linea, game)
      }, 500);
    } else {
      setTimeout(()=>{
        if (this.isYou(this.gameStatus.player1)) {
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
        }
      },100)
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
