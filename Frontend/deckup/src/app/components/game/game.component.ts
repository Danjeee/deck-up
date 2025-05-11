import { AfterViewInit, Component, HostListener, OnDestroy, OnInit } from '@angular/core';
import { environmentsURLs } from '../../utils/environmentsURls';
import { AlertService } from '../../services/alert.service';
import { Router } from '@angular/router';
import { GameService } from '../../services/game.service';
import { UserSession } from '../../utils/UserSession';
import { CommonModule } from '@angular/common';
import { animate, createDraggable, createSpring, stagger, utils } from 'animejs';
import { ParticleComponent } from '../particle/particle.component';
import { css, Localizer } from '../../utils/Utils';

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

  constructor(private alert: AlertService, private router: Router, private service: GameService, private translator: Localizer ) {
    super()
  }

  joinlistener(id: any) {
    this.service.joinListener(id)
    this.loaded = false;
    this.service.getStatus().subscribe((status: any) => {
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
                this.loaded = true
              }
            } else {
              status = (status + "").split("/")
              if ((status[0] + "").startsWith("l")) {
                if (status[1] != "put") {
                  this.triggerExplosion(document.getElementById(status[0]) as HTMLElement, status[1] == "heal" ? "#198754" : "#d9534f")
                } else {
                  let comm = data[status[0]].carta.habilidadDTO.especial + ""
                  if (comm != null) {
                    comm = comm.replace(/\d+/g, '')
                    if (comm.startsWith("D")) {
                      this.gameStatus.command = status;
                      this.renderDeployEffects(comm, this.gameStatus)
                    }
                  }
                }
              }
              if ((status[0] + "").startsWith("p")) {
                const player = document.getElementById(status[0]) as HTMLElement
                this.triggerPlayer(player, status)
              }
              if (status[0] == "turndmg") {
                const lines = Array.from(status).splice(1)
                lines.forEach((line: any) => {
                  Object.keys(this.gameStatus).forEach((key: any) => {
                    if ((key + "").startsWith("l")) {
                      if (this.gameStatus[key] != null) {
                        if (this.gameStatus[key].id == line) {
                          this.triggerExplosion(document.getElementById(key) as HTMLElement)
                        }
                      }
                    }
                  })
                });
              }
              this.renderDeaths(data)
              this.gameStatus = data;
              data.command = status;
              this.renderAll(data)
              this.loaded = true
            }
          }
        })
      }
    })
  }

  triggerPlayer(player: any, status: any) {
    this.triggerExplosion(player, status == "heal" ? "#198754" : "#d9534f")
    if (status == "heal") {
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

  ngAfterViewInit(): void {
    this.gameStatus = null
    this.translator.set({
      vida: "Vida",
       stun: "Aturdido",
       stun_name: "Tipo de aturdimiento",
       burn: "Quemado",
       poisn: "Envenenado",
       bleed: "Sangrado",
       prcnt_up: "Mejora de daño",
       prcnt_dwn: "Debilidad", 

       hab_freeze: "Aturdimiento",
       hab_freeze_name: "Tipo de aturdimiento",
       hab_burn: "Quemado",
       hab_heal: "Curación",
       hab_poisn: "Envenenado",
       hab_bleed: "Sangrado",
       hab_prcnt_up: "Mejora de daño",
       hab_prcnt: "Daño de porcentaje",
       hab_prcnt_dwn: "Debilidad", 
       hab_crit: "Porcentaje de critico",
       hab_critMult: "Mejora de daño critico",
       hab_leth: "Letalidad",
       hab_esq: "Porcentaje de evasión",
       hab_load_atq: "wip",
       hab_dmg: "Daño"
    })
    if (sessionStorage.getItem("game") == null || sessionStorage.getItem("game") == "") {
      this.router.navigate(["/home"])
    } else {
      setTimeout(() => {
        this.joinlistener(sessionStorage.getItem("game"))
      }, 100);
      this.service.getGame(sessionStorage.getItem("game")).subscribe({
        next: (data) => {
          if (data.id == sessionStorage.getItem("game")) {
            this.renderAll(data)
          }
        }
      })
    }
  }
  renderDeaths(data: any) {
    Object.keys(this.gameStatus).forEach((key: any) => {
      if ((key + "").startsWith("l")) {
        if (data[key] == null && this.gameStatus[key] != null) {
          if (this.gameStatus[key].vida > 0) {
            if (this.gameStatus[key].carta.habilidadDTO.especial != null) {
              let com
              if (key.charAt(1) == 1){
                com = "l2_" + key.charAt(key.length-1)
              } else {
                com = "l1_" + key.charAt(key.length-1)
              }
              this.gameStatus.command = [com]
              this.renderDieEffects(this.gameStatus[key].carta.habilidadDTO.especial, this.gameStatus)
            }
          }
        }
      }
    })
  }
  async renderAll(data: any) {
    this.loaded = await this.rendergame(data);
    if (data.command != null) {
      setTimeout(() => {
        if (data.command[1] == "put") {
          this.animatedrop(data.command[0])
        }
      }, 0);
    }
  }

  async rendergame(data: any): Promise<boolean> {
    if (data.status != 'activo') {
      if (String(data.status).startsWith("winner: ")) {
        this.victoria(String(data.status).substring(String(data.status).indexOf(":") + 1))
      }
    }
    this.gameStatus = data

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
        if (this.yourturn) {
          this.animatecards(document.querySelectorAll('.card'), this.mana)
          this.animatespells(document.querySelectorAll('.spell'), this.mana)
          this.animatespells_all(document.querySelectorAll('.spell_all'), this.mana)
          this.animatespells_self(document.querySelectorAll('.self_spell'), this.mana)
        }
        //this.renderStatusEffects()
        this.checkStatus()
      }, 200);
    }
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve(true)
      }, 100);
    })
  }

  renderDeployEffects(comm: any, data: any) {
    let enemy = ""
    switch (comm) {
      case "DD":
      case "DP":
      case "DF":
      case "DB":
      case "DK":
        enemy = (data.command[0] + "").split("_")[0].charAt(1)
        enemy = (enemy == "1") ? "2" : "1"
        let line = (data.command[0] + "").split("_")[1]
        if (data["l" + enemy + "_" + line] != null) {
          this.triggerExplosion(document.getElementById("l" + enemy + "_" + line) as HTMLElement, "#d9534f")
        }
        break;
      case "DFA":
      case "DDA":
      case "DBA":
      case "DKA":
      case "DPA":
        enemy = (data.command[0] + "").split("_")[0].charAt(1)
        enemy = (enemy == "1") ? "2" : "1"
        Object.keys(data).forEach((key: any) => {
          key = key + ""
          if (key.startsWith("l")) {
            if (key.charAt(1) == enemy && data[key] != null) {
              this.triggerExplosion(document.getElementById(key) as HTMLElement, "#d9534f")
            }
          }
        })
        break;
      case "DHA":
        enemy = (data.command[0] + "").split("_")[0].charAt(1)
        this.triggerPlayer(document.getElementById("player" + enemy) as HTMLElement, "heal")
        this.healAll(enemy, data)
        break;
      case "DHC":
        this.healAll(enemy, data)
        break;
      case "DHP":
        enemy = (data.command[0] + "").split("_")[0].charAt(1)
        this.triggerPlayer(document.getElementById("player" + enemy) as HTMLElement, "heal")
        break;
    }
  }

  renderDieEffects(comm: any, data: any) {
    let enemy = ""
    comm = comm.replace(/\d+/g, '')
    switch (comm) {
      case "TD":
      case "TP":
      case "TF":
      case "TB":
      case "TK":
        enemy = (data.command[0] + "").split("_")[0].charAt(1)
        let line = (data.command[0] + "").split("_")[1]
        if (data["l" + enemy + "_" + line] != null) {
          this.triggerExplosion(document.getElementById("l" + enemy + "_" + line) as HTMLElement, "#d9534f")
        }
        break;
      case "TFA":
      case "TDA":
      case "TBA":
      case "TKA":
      case "TPA":
        enemy = (data.command[0] + "").split("_")[0].charAt(1)
        Object.keys(data).forEach((key: any) => {
          key = key + ""
          if (key.startsWith("l")) {
            if (key.charAt(1) == enemy && data[key] != null) {
              this.triggerExplosion(document.getElementById(key) as HTMLElement, "#d9534f")
            }
          }
        })
        break;
      case "TY":
        enemy = (data.command[0] + "").split("_")[0].charAt(1)
        this.triggerPlayer(document.getElementById("player" + enemy) as HTMLElement, "dmg")
        break;
    }
  }
  healAll(enemy: any, data: any) {
    enemy = (data.command[0] + "").split("_")[0].charAt(1)
    Object.keys(data).forEach((key: any) => {
      key = key + ""
      if (key.startsWith("l")) {
        if (key.charAt(1) == enemy && key != data.command[0] && data[key] != null) {
          this.triggerExplosion(document.getElementById(key) as HTMLElement, "#198754")
        }
      }
    })
  }

  animatedrop(line: any) {
    const parent = document.getElementById(line) as HTMLElement
    const div = parent.querySelector<HTMLElement>(".troop") as HTMLElement
    this.fallAndSquash(div)
  }
  fallAndSquash(div: HTMLElement) {
    let pos = -300;
    const target = 0;
    const speed = 10;

    function fall() {
      if (pos < target) {
        pos += speed;
        div.style.position = "absolute"
        div.style.top = pos + 'px';
        requestAnimationFrame(fall);
      } else {
        squash();
      }
    }

    function squash() {
      div.style.transition = 'transform 0.15s ease-out';
      div.style.transform = 'scaleX(1.2) scaleY(0.8)';

      setTimeout(() => {
        rebound();
      }, 150);
    }

    function rebound() {
      div.style.transition = 'transform 0.2s ease-out';
      div.style.transform = 'scaleX(1) scaleY(1)';
      setTimeout(() => {
        div.style.transition = 'scale 0.5s ease-in-out';
      }, 200);
    }

    fall();
  }

  private triggerExplosion(element: HTMLElement, color: string = "#000000") {
    const rect = element.getBoundingClientRect();
    ParticleComponent.animejs_explosion(rect.width / 2 + rect.left, rect.height + rect.top, color);
  }

  renderStatusEffects() {
    for (let player = 1; player <= 2; player++) {
      for (let line = 1; line <= 5; line++) {
        let key = `l${player}_${line}`;
        if (this.gameStatus[key] != null) {
          if (this.gameStatus[key].stun != null) {
            if (this.gameStatus[key].stun != 0) {
              (document.getElementById(key) as HTMLElement).classList.add('status');
              (document.getElementById(key) as HTMLElement).classList.add('frozen');
            }
          }
          if (this.gameStatus[key].bleed != null) {
            if (this.gameStatus[key].bleed != 0) {
              (document.getElementById(key) as HTMLElement).classList.add('status');
              (document.getElementById(key) as HTMLElement).classList.add('bleed');
              if (this.gameStatus.turno == 3 && this.gameStatus.p1_c == null) {
                this.triggerExplosion(document.getElementById(key) as HTMLElement)
              }
            }
          }
          if (this.gameStatus[key].poisn != null) {
            if (this.gameStatus[key].poisn != 0) {
              (document.getElementById(key) as HTMLElement).classList.add('status');
              (document.getElementById(key) as HTMLElement).classList.add('poisn');

              if (this.gameStatus.turno == 3 && this.gameStatus.p1_c == null) {
                this.triggerExplosion(document.getElementById(key) as HTMLElement)
              }
            }
          }
          if (this.gameStatus[key].burn != null) {
            if (this.gameStatus[key].burn != 0) {
              (document.getElementById(key) as HTMLElement).classList.add('status');
              (document.getElementById(key) as HTMLElement).classList.add('burn');
            }
            if (this.gameStatus.turno == 3 && this.gameStatus.p1_c == null) {
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
      if (game.player2.carta1 != null) { count.push(0) }
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
    this.gameStatus.turn = -1;
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

  showstatus(line: any, e: MouseEvent) {
    const id = "status" + Math.random() * 999
    const modal = document.createElement('div')
    modal.className = "modal btn " + (line.carta.rarezaDTO.nombre == "???" ? "idk" : line.carta.rarezaDTO.nombre)
    modal.id = id
    css(modal, {
      width: "auto",
      height: "200px",
      overflowY: "auto",
      overflowX: "hidden",
      position: "fixed",
      transform: "translate(-50%, -50%) scale(1)",
      zIndex: "10",
      left: (e.clientX) + "px",
      top: (e.clientY) + "px",
      transition: "translate .5s",
      transformOrigin: "center center",
      padding: "1em",
      textAlign: "center",
      scrollbarColor: "#FFFFFF80 transparent",
      display: "flex",
      flexDirection: "column",
      justifyContent: "start",
    })
    const tit = document.createElement('h1')
    tit.innerHTML = line.carta.nombre
    css(tit, {
      color: "#13253e",
      pointerEvents: "none"
    })
    modal.appendChild(tit)
    Object.keys(line).forEach((key) => {
      if (line[key] != null && line[key] != 0 && key != "game" && key != "willcrit" && key != "id" && key != "carta") {
        let attr = document.createElement("h3")
        attr.className = "str"
        css(attr, {
          color: "#13253e",
          margin: "0",
          pointerEvents: "none"
        })
        attr.innerHTML = this.translator.get(key) + ": " + line[key]
        modal.appendChild(attr)
      }
    })
    const hab = document.createElement('h1')
    hab.innerHTML = "Habilidad: " + line.carta.habilidadDTO.nombre
    css(hab, {
      color: "#13253e",
      pointerEvents: "none"
    })
    modal.appendChild(hab)
    const habilidad = line.carta.habilidadDTO
    Object.keys(line.carta.habilidadDTO).forEach((key) => {
      if (habilidad[key] != null && habilidad[key] != 0 && key != "nombre" && key != "id" && key != "descripcion" && key != "especial" && key != "color" && key != "entorno") {
        let attr = document.createElement("h3")
        attr.className = "str"
        css(attr, {
          color: "#13253e",
          margin: "0",
          pointerEvents: "none"
        })
        let p = ""
        if (key.includes("prcnt")) { p = "%" }
        if (key.includes("crit")) { p = "%" }
        attr.innerHTML = this.translator.get("hab_"+key) + ": " + habilidad[key] + p
        modal.appendChild(attr)
      }
    })

    let mousein = false;
    modal.addEventListener('mouseenter', () => {
      mousein = true
    })
    modal.addEventListener('mouseleave', () => {
      mousein = false;
      setTimeout(() => {
        if (!mousein) {
          css(modal, {
            pointerEvents: "none"
          })
          modal.animate([
            { transform: "translate(-50%, -50%) scale(1)", transformOrigin: "center center" },
            { transform: "translate(-50%, -50%) scale(0)", transformOrigin: "center center" }
          ], {
            duration: 400,
            easing: "linear"
          })
          setTimeout(() => {
            modal.remove()
          }, 400);
        }
      }, 2000);
    })
    modal.addEventListener('click', () => {
      css(modal, {
        pointerEvents: "none"
      })
      modal.animate([
        { transform: "translate(-50%, -50%) scale(1)", transformOrigin: "center center" },
        { transform: "translate(-50%, -50%) scale(0)", transformOrigin: "center center" }
      ], {
        duration: 400,
        easing: "linear"
      })
      setTimeout(() => {
        modal.remove()
      }, 399);
    })
    document.body.appendChild(modal)
    modal.animate([
      { transform: "translate(-50%, -50%) scale(0)", transformOrigin: "center center" },
      { transform: "translate(-50%, -50%) scale(1)", transformOrigin: "center center" }
    ], {
      duration: 500,
      easing: "linear"
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

  checkStatus() {
    for (let player = 1; player <= 2; player++) {
      for (let line = 1; line <= 5; line++) {
        let key = `l${player}_${line}`;
        let count = 0;
        if (this.gameStatus[key] != null) {
          if (this.gameStatus[key].bleed == null || this.gameStatus[key].bleed == 0) {
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
          if (count == 4) {
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

  checkFreeze(key: string) {
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
    let crit = "false"
    if (from.willcrit != null) {
      if (from.willcrit == true) {
        crit = "#ffffff"
      }
    } else {
      crit = from.carta.habilidadDTO.color
    }
    this.shoot(shooter, line, crit);
    const playerFrom = playerTarget.charAt(playerTarget.length - 1) == "1" ? "2" : "1"
    const lineFinal = "l" + playerFrom + "_" + line.charAt(1)
    setTimeout(() => {
      if (to) {
        to.vida = this.realizarDmgs(from, to.vida, to.carta.vida, to.carta.habilidadDTO, lineFinal)
      } else {
        this.gameStatus[playerTarget].vida = this.realizarDmgs(from, this.gameStatus[playerTarget].vida, 40)
      }
    }, 500);
  }

  realizarDmgs(from: any, vida: any, max: any, hab: any = null, line: any = null): number {
    if (from.carta.habilidadDTO.leth != null && vida > 0) {
      vida -= (max - vida) * from.carta.habilidadDTO.leth / 100
    }
    vida -= from.carta.habilidadDTO.dmg
    if (from.carta.habilidadDTO.prcnt != null) {
      vida -= max * from.carta.habilidadDTO.prcnt / 100
    }
    if (from.willcrit) {
      vida -= (from.carta.habilidadDTO.dmg * from.carta.habilidadDTO.critMult / 100);
    }
    if (vida < 0) {
      vida = 0
    }
    if (hab != null) {
      if (vida == 0 && hab.especial != null) {
        if ((hab.especial + "").startsWith("T")) {
          const comm = hab.especial
          this.gameStatus.command = [line]
          this.renderDieEffects(comm, this.gameStatus)
        }
      }
    }
    return vida;
  }

  private updateLineUI(currentLine: string, previousLine?: string): void {
    if (currentLine != "none") {
      (document.getElementById(currentLine) as HTMLElement).className = "line selectedline";
    }
    if (previousLine) {
      (document.getElementById(previousLine) as HTMLElement).className = "line";
    }
  }

  renderAllHeals() {
    Object.keys(this.gameStatus).forEach((key: any) => {
      if ((key + "").startsWith("l")) {
        if (this.gameStatus[key] != null) {
          if (this.gameStatus[key].vida > 0 && this.gameStatus[key].carta.habilidadDTO.heal != 0 && this.gameStatus[key].carta.habilidadDTO.heal != null
            && (this.gameStatus[key].stun == 0 || this.gameStatus[key].stun == null)
          ) {
            let from = this.gameStatus[key]
            if (from.carta.habilidadDTO.heal != null && from.carta.habilidadDTO.heal != 0 && from.vida < from.carta.vida) {
              const fromHTML = (document.getElementById(key) as HTMLElement).getBoundingClientRect()
              ParticleComponent.animejs_explosion(fromHTML.x + fromHTML.width / 2, fromHTML.y + fromHTML.height / 2, "#198754")
              from.vida = (from.vida + from.carta.habilidadDTO.heal) > from.carta.vida ? from.carta.vida : (from.vida + from.carta.habilidadDTO.heal)
            }
          }
        }
      }
    })

  }
  fightLoop(linea: any, game: any) {
    if (linea != -1) {
      setTimeout(() => {
        this.gameStatus = game
        linea = this.fight(linea, game)
        this.fightLoop(linea, game)
      }, 500);
    } else {
      setTimeout(() => {
        this.renderAllHeals()
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
      }, 200)
    }
  }

  turn3(game: any = this.gameStatus) {
    this.fightLoop(1, game)
  }
  shoot(where: any, line: any, color: string = "#000000") {
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
      part.style.backgroundColor = color + "80"
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
          ParticleComponent.animejs_explosion(linea.getBoundingClientRect().left + linea.getBoundingClientRect().width / 2, linea.getBoundingClientRect().bottom, color)
        }, 450);
        break;
      case 'oponent_c':
        y = linea.getBoundingClientRect().top
        // finalY = -linea.getBoundingClientRect().top*3
        x = linea.getBoundingClientRect().left + linea.getBoundingClientRect().width / 2 - shoot.getBoundingClientRect().width / 2
        shoot.style.top = y + "px"
        shoot.style.left = x + "px"
        this.animateShot(animated, shoot, true)
        setTimeout(() => {
          ParticleComponent.animejs_explosion(linea.getBoundingClientRect().left + linea.getBoundingClientRect().width / 2, linea.getBoundingClientRect().top * 1.5, color)
        }, 450);
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
          ParticleComponent.animejs_explosion(linea.getBoundingClientRect().left + linea.getBoundingClientRect().width / 2, linea.getBoundingClientRect().top * 1.5 - 50, color)
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
          ParticleComponent.animejs_explosion(linea.getBoundingClientRect().left + linea.getBoundingClientRect().width / 2, linea.getBoundingClientRect().bottom + 50, color)
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
