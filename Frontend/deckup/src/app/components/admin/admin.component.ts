import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { TiendaService } from '../../services/tienda.service';
import { AlertService } from '../../services/alert.service';

@Component({
  selector: 'app-admin',
  imports: [],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.css'
})
export class AdminComponent {

  constructor(private router: Router, private tservice: TiendaService, private alert: AlertService){}

  goto(route: string) {
    this.router.navigate(['admin/'+route])
  }

  changeTienda(){
    this.tservice.change().subscribe({
      next: (data) => {
        if (data.status == 200){
          this.alert.success(data.tit,data.msg)
        } else {
          this.alert.error(data.tit, data.msg)
        }
      }
    })
  }

}
