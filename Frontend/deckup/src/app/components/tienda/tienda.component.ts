import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { TiendaService } from '../../services/tienda.service';
import { User } from '../../utils/User';
import { UserSession } from '../../utils/UserSession';
import { environmentsURLs } from '../../utils/environmentsURls';

@Component({
  selector: 'app-tienda',
  imports: [CommonModule],
  templateUrl: './tienda.component.html',
  styleUrl: './tienda.component.css'
})
export class TiendaComponent extends environmentsURLs implements OnInit  {

  cards: any
  packs: any
  user: User = UserSession.getUser() 

  constructor(private service: TiendaService){
    super()
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
