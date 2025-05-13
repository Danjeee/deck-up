import { Component, HostListener, OnInit } from '@angular/core';
import { TradeService } from '../../services/trade.service';
import { AlertService } from '../../services/alert.service';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { QRCodeComponent } from 'angularx-qrcode';

@Component({
  selector: 'app-trade',
  imports: [CommonModule, QRCodeComponent],
  templateUrl: './trade.component.html',
  styleUrl: './trade.component.css'
})
export class TradeComponent implements OnInit {

  waiting = false;
  code = "";
  qrValue: string | null = null;

  constructor(private service: TradeService, private alert: AlertService, private router: Router, private route: ActivatedRoute) { }
  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      const code = params.get('code')
      if (code != null) {
        this.instajoin(code)
      }
    });
  }
  joinlistener(id: any) {
    this.service.joinListener(id)
    this.service.getStatus().subscribe((status: any) => {
      if (status != "") {
        this.service.disconnect()
        this.router.navigate([`trade/${this.code}`])
      }
    })
  }
  create() {
    this.service.create().subscribe({
      next: (data) => {
        if (data.status == 200) {
          let res = (data.msg + "").split("/")
          // const url = `https://deckup.tecnobyte.com/trade?code=${res[1]}`;
          const url = `http://localhost:4200/trade?code=${res[1]}`;
          this.qrValue = url;
          this.code = res[1]
          sessionStorage.setItem("trade", res[0])
          this.waiting = true;
          this.joinlistener(res[0])
        } else {
          this.alert.error(data.tit, data.msg)
        }
      }
    })
  }
  instajoin(code: any) {
    this.service.join(code).subscribe({
      next: (data) => {
        if (data.status == 200) {
          let res = (data.msg + "").split("/")
          sessionStorage.setItem("trade", res[0])
          this.router.navigate([`trade/${res[1]}`])
        } else {
          this.alert.error(data.tit, data.msg)
        }
      }
    })
  }
  join() {
    this.alert.ask("Unirse a sala", "Escribe el codigo de la sala").then((res) => {
      const resp = res.value
      if (resp != "") {
        this.service.join(resp).subscribe({
          next: (data) => {
            if (data.status == 200) {
              let res = (data.msg + "").split("/")
              sessionStorage.setItem("trade", res[0])
              this.router.navigate([`trade/${res[1]}`])
            } else {
              this.alert.error(data.tit, data.msg)
            }
          }
        })
      }
    })
  }
  back() {
    this.service.cancel().subscribe({
      next: (data) => {
        if (data.status == 200) {
          this.service.disconnect()
          this.code = ""
          sessionStorage.removeItem("trade")
          this.waiting = false
        } else {
          this.alert.error(data.tit, data.msg)
        }
      }
    })
  }

  @HostListener('window:beforeunload', ['$event'])
  instantlose($event: BeforeUnloadEvent) {
    sessionStorage.removeItem("game")
    this.service.cancel().subscribe({
      next: (data) => {
        console.log(data)
      }
    })
  }

}
