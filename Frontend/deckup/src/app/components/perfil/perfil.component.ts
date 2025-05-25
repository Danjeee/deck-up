import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { environmentsURLs } from '../../utils/environmentsURls';
import { UserService } from '../../services/user.service';
import { forkJoin } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-perfil',
  imports: [CommonModule],
  templateUrl: './perfil.component.html',
  styleUrl: './perfil.component.css'
})
export class PerfilComponent extends environmentsURLs implements OnInit {

  user: any
  loaded: boolean = false

  constructor(private service: UserService, private router: Router) {
    super()
  }

  ngOnInit(): void {
    let id = this.router.url.split("/")[this.router.url.split("/").length - 1]
    forkJoin([
      this.service.findById(Number(id)),
      this.service.findCards(Number(id)),
      this.service.findGames(Number(id)),
      this.service.findFriends(Number(id)),
      this.service.findTrades(Number(id)),

    ]).subscribe(([data1, data2, data3, data4, data5]) => {
      this.user = data1
      this.user.cartas = data2[0]
      this.user.cartas_ex = data2[1]
      this.user.gamesw = data3[0]
      this.user.gamesl = data3[1]
      this.user.friends = data4
      this.user.trades = data5
      this.loaded = true
    })
  }

  playerStatus(): string {
    if (this.user.currently_logged) {
      return "En linea"
    }
    const fecha = new Date(this.user.last_login);
    const hoy = new Date();

    const normalizar = (d: Date) => new Date(d.getFullYear(), d.getMonth(), d.getDate());

    const fechaNormal = normalizar(fecha);
    const hoyNormal = normalizar(hoy);

    const msPorDia = 1000 * 60 * 60 * 24;
    const diferenciaDias = Math.floor((hoyNormal.getTime() - fechaNormal.getTime()) / msPorDia);

    if (diferenciaDias === 0) {
      return "Ult. vez Hoy";
    } else if (diferenciaDias === 1) {
      return "Ult. vez Ayer";
    } else if (diferenciaDias > 1) {
      return `Ult. vez hace ${diferenciaDias} días`;
    } else {
      return "";
    }
  }

}
