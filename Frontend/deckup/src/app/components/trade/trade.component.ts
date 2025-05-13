import { Component, HostListener } from '@angular/core';
import { TradeService } from '../../services/trade.service';
import { AlertService } from '../../services/alert.service';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-trade',
  imports: [CommonModule],
  templateUrl: './trade.component.html',
  styleUrl: './trade.component.css'
})
export class TradeComponent {

  waiting = false;
  code = "";

  constructor(private service: TradeService, private alert: AlertService, private router: Router){}
  joinlistener(id: any) {
    this.service.joinListener(id)
    this.service.getStatus().subscribe((status: any) => {
      if (status != ""){
        this.service.disconnect()
        this.router.navigate([`trade/${this.code}`])
      }
    })
  }
  create(){
    this.service.create().subscribe({
      next: (data) => {
        if (data.status == 200){
          let res = (data.msg + "").split("/")
          this.code = res[1]
          sessionStorage.setItem("trade", res[0]) 
          this.waiting = true;
          this.joinlistener(res[0])
        } else {
          this.alert.error(data.tit,data.msg)
        }
      }
    })
  }
  join(){
    this.alert.ask("Unirse a sala", "Escribe el codigo de la sala").then((res) => {
      const resp = res.value
      if (resp != ""){
        this.service.join(resp).subscribe({
          next: (data) => {
            if (data.status == 200){
              let res = (data.msg + "").split("/")
              sessionStorage.setItem("trade", res[0])
              this.router.navigate([`trade/${res[1]}`])
            }
          }
        })
      }
    })
  }
  back(){
  this.service.cancel().subscribe({
      next: (data) => {
        if (data.status == 200){
          this.service.disconnect()
          this.code = ""
          sessionStorage.removeItem("trade") 
          this.waiting = false
        } else {
          this.alert.error(data.tit,data.msg)
        }
      }
    })
  }

  @HostListener('window:beforeunload', ['$event'])
    instantlose($event: BeforeUnloadEvent){
      sessionStorage.removeItem("game")
      this.service.cancel().subscribe({
        next: (data) => {
          console.log(data)
        }
      })
    }

}
