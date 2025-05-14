import { Component } from '@angular/core';
import { TradeService } from '../../services/trade.service';
import { Router } from '@angular/router';
import { AlertService } from '../../services/alert.service';
import { CommonModule } from '@angular/common';
import { environmentsURLs } from '../../utils/environmentsURls';

@Component({
  selector: 'app-traderoom',
  imports: [CommonModule],
  templateUrl: './traderoom.component.html',
  styleUrl: './traderoom.component.css'
})
export class TraderoomComponent extends environmentsURLs {

  you: any
  loaded: boolean = false

  trade: any

  yourcards: any

  constructor(private service: TradeService, private router: Router, private alert: AlertService){
    super()
  }
  joinlistener(id: any) {
    this.service.joinListener(id)
    this.service.getStatus().subscribe((status: any) => {
      if (status == "leave") {
        this.service.disconnect()
        this.router.navigate([`trade`])
      }
    })
  }
  ngOnInit(): void {
    this.joinlistener(sessionStorage.getItem("trade"))
    this.service.getTrade(sessionStorage.getItem("trade")).subscribe({
      next: (data) =>{
        if (data != null){
          this.trade = data
          this.service.findAllPlayerCards().subscribe({
            next: (resp) => {
              this.yourcards = resp
              console.log(resp)
              this.loaded = true
            }
          })
        } else {
          this.alert.error("Error", "Ha habido un problema recuperando el intercambio").then(()=>{
            this.router.navigate(['trade'])
          })
        }
      }
    })
  }
  other(){
    
  }
}
