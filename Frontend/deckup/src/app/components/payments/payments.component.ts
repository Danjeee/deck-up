import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { PaymentService } from '../../services/payment.service';

@Component({
  selector: 'app-payments',
  imports: [],
  templateUrl: './payments.component.html',
  styleUrl: './payments.component.css'
})
export class PaymentsComponent implements OnInit {
    constructor(private router: Router, private service: PaymentService){}
    ngOnInit(): void {
        if (this.router.url == "/tienda/cancel") {
          this.router.navigate(['/tienda'])
        } else {
          this.service.verifyPayments().subscribe({
            next: (data) => {
              console.log(data)
            }
          })
        }
    }
}
