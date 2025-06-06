import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { TiendaService } from '../../services/tienda.service';
import { User } from '../../utils/User';
import { UserSession } from '../../utils/UserSession';
import { environmentsURLs } from '../../utils/environmentsURls';
import { AlertService } from '../../services/alert.service';
import { ParticleComponent } from '../particle/particle.component';
import { PackComponent } from '../pack/pack.component';
import { PaymentService } from '../../services/payment.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-tienda',
  imports: [CommonModule],
  templateUrl: './tienda.component.html',
  styleUrl: './tienda.component.css'
})
export class TiendaComponent extends environmentsURLs implements OnInit {

  selecteed: string = "cards"
  cards: any
  packs: any
  gems: any
  loading: boolean = false
  user: User = UserSession.getUser()
  currency = 'EUR';
  time_left_s: number = 0
  time_left_m: number = 0
  time_left_h: number = 0

  constructor(private service: TiendaService, private alert: AlertService, private paymentService: PaymentService, private router: Router) {
    super()
  }

  isselected(table: string): string {
    return table == this.selecteed ? "selected bg-p" : "bg-s";
  }
  toggle(table: string) {
    this.selecteed = table
    localStorage.setItem("tienda", table)
  }
  buy(card: any) {
    this.alert.confirm('Confirmar compra', `Estas seguro de comprar ${card.nombre} por ${card.precio} gemas?`, () => {
      this.service.buy(card.id).subscribe({
        next: (data) => {
          if (data.status == 200) {
            this.alert.success(data.tit, data.msg)
            UserSession.pay(card.precio)
            this.user = UserSession.getUser()
            ParticleComponent.getCard(card)
          } else {
            this.alert.error(data.tit, data.msg)
          }
        }
      })
    })
  }
  buy_pack(pack: any) {
    this.alert.confirm('Confirmar compra', `Estas seguro de comprar ${pack.nombre} por ${pack.precio} gemas?`, () => {
      this.service.buy_pack(pack.id).subscribe({
        next: (data) => {
          if (data.status == 200) {
            PackComponent.showRecivedCards(data.pack, data.cartas)
            UserSession.pay(pack.precio)
          } else {
            this.alert.error(data.tit, data.msg)
          }
        }
      })
    })
  }

  countdown() {
    setTimeout(() => {
      const newTienda = new Date(
        new Date().getFullYear(),
        new Date().getMonth(),
        new Date().getDate() + 1,
        0, 0, 0, 0
      );
      if (new Date() >= newTienda) {
        setTimeout(() => {
          if(this.router.url == "/tienda"){
            window.location.reload()
          }
        }, 2000);
      } else {
        const time_left = newTienda.getTime() - new Date(Date.now()).getTime()
        var s = Math.floor(time_left / 1000);
        var m = Math.floor(s / 60);
        var h = Math.floor(m / 60);
        h %= 24;
        m %= 60;
        s %= 60;

        this.time_left_h = h;
        this.time_left_m = m;
        this.time_left_s = s;
        this.countdown()
      }
    }, 1000);
  }

  ngOnInit(): void {
    this.countdown()
    if (localStorage.getItem("tienda")) {
      this.selecteed = localStorage.getItem("tienda") as string
    }
    this.service.get().subscribe({
      next: (data) => {
        this.cards = [data.carta1, data.carta2, data.carta3, data.carta4, data.carta5]
        this.packs = [data.paq3, data.paq2, data.paq1]
      }
    })
    this.service.getgems().subscribe({
      next: (data) => {
        this.gems = data
      }
    })
  }
  buygems(gemoffer: any) {
    this.alert.confirm('Confirmar compra', `Estas seguro de comprar ${gemoffer.nombre} por ${gemoffer.precio}€?`, () => {
      this.loading = true
      this.paymentService
        .createPayment(gemoffer.precio, this.currency, gemoffer.nombre, this.paymentVerifyUrl, this.paymentCancelUrl, UserSession.getUser().auth, gemoffer.cant)
        .subscribe({
          next: (response: any) => {
            window.location.href = response.links.find((link: any) => link.rel === 'approve').href;
          },
          error: (err) => console.error('Error al crear el pago:', err),
        });

    })
  }


}
