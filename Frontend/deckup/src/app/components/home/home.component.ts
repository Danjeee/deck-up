import { AfterViewInit, Component, OnInit } from '@angular/core';
import { UserSession } from '../../utils/UserSession';
import { CommonModule } from '@angular/common';
import { UserService } from '../../services/user.service';
import { AlertService } from '../../services/alert.service';
import { User } from '../../utils/User';
import { NavComponent } from '../nav/nav.component';
import { ParticleComponent } from '../particle/particle.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {


  constructor(private service: UserService, private alert: AlertService, private router: Router){}

  ngOnInit(): void {
    setTimeout(() => {
      const user_np = new Date(UserSession.getUser().pay)
      if (user_np < new Date()) {
        this.can_pay = true
      } else {
        this.can_pay = false
        this.countdown()
      }
    }, 1);
  }

  usuario = UserSession.getUser()

  time_left_s: number = 0
  time_left_m: number = 0
  time_left_h: number = 0

  can_pay: boolean = false

  pay_anim(){
    for (let i = 0; i < 100; i++) {
      ParticleComponent.generateGems([47,47])
    }
    const currency = document.getElementById("currency_txt") as HTMLElement
    setTimeout(() => {
      for (let i = Number.parseInt(currency.innerHTML); i < (UserSession.getUser().currency as number); i++) {
        setTimeout(() => {
          currency.innerHTML = (Number.parseInt(currency.innerHTML) + 1) +""
        }, 50);
      }
    }, 2000);
  }

  goto(locat: string) {
    this.router.navigate([locat])
    }

  get_paid() {
    this.service.getPaid().subscribe({
      next: (data) => {
        if (data.status == 200) {
          this.pay_anim()
          this.alert.pago_recibido(data.msg)
          this.can_pay = false
          const user: User = UserSession.getUser()
          user.pay = data.user.nextPayment
          user.currency = data.user.currency
          UserSession.setUser(user)
          this.countdown()
        } else {
          this.alert.error(data.tit, data.msg)
        }
      }
    })
  }

  countdown() {
    setTimeout(() => {
      const user_np = new Date(UserSession.getUser().pay)
      if (new Date() > user_np) {
        this.can_pay = true
      } else {
        const time_left = user_np.getTime() - new Date(Date.now()).getTime()
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

}
