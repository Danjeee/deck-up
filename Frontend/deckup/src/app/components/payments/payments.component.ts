import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { PaymentService } from '../../services/payment.service';
import { AlertService } from '../../services/alert.service';
import { UserSession } from '../../utils/UserSession';
import { ParticleComponent } from '../particle/particle.component';

@Component({
  selector: 'app-payments',
  imports: [],
  templateUrl: './payments.component.html',
  styleUrl: './payments.component.css'
})
export class PaymentsComponent implements OnInit {
    constructor(private router: Router, private service: PaymentService, private alert: AlertService){}
    ngOnInit(): void {
        console.log(this.router.url)
        if (this.router.url.includes("?")) {
          this.router.navigate([this.router.url.substring(0,this.router.url.lastIndexOf("?"))])
        }
        setTimeout(() => {
          if (this.router.url == "/tienda/cancel") {
            this.router.navigate(['/tienda'])
          } else {
            this.service.verifyPayments().subscribe({
              next: (data) => {
                if (data.status == 200) {
                  this.router.navigate(['/tienda'])
                  setTimeout(() => {
                    for (let i = 0; i < 100; i++) {
                      ParticleComponent.generateGems([47,47])
                    }
                    this.alert.pago_recibido(data.cant)
                    setTimeout(() => {
                      UserSession.get_paid(data.cant)
                    }, 2000);
                  }, 100);
                } else {
                  this.router.navigate(['/tienda'])
                }
              }
            })
          }
        }, 1);
    }
}
