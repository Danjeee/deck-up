import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { TiendaService } from '../../services/tienda.service';
import { User } from '../../utils/User';
import { UserSession } from '../../utils/UserSession';
import { environmentsURLs } from '../../utils/environmentsURls';
import { AlertService } from '../../services/alert.service';

@Component({
  selector: 'app-tienda',
  imports: [CommonModule],
  templateUrl: './tienda.component.html',
  styleUrl: './tienda.component.css'
})
export class TiendaComponent extends environmentsURLs implements OnInit  {

  selecteed: string = "cards"
  cards: any
  packs: any
  user: User = UserSession.getUser() 

  constructor(private service: TiendaService, private alert: AlertService){
    super()
  }

  isselected(table: string) : string{
    return table == this.selecteed ? "selected bg-p" : "bg-s";
  }
  toggle(table: string){
    this.selecteed = table
  }
  buy(id: number){
    this.service.buy(id).subscribe({
      next: (data) => {
        if (data.status == 200) {
          this.alert.success(data.tit, data.msg)
        } else {
          this.alert.error(data.tit, data.msg)
        }
      }
    })
  }

  ngOnInit(): void {
      this.service.get().subscribe({
        next: (data) =>{
          console.log(data)
          this.cards = [data.carta1,data.carta2,data.carta3,data.carta4,data.carta5]
          this.packs = [data.paq1, data.paq2, data.paq3]
        }

      })
  }

}
