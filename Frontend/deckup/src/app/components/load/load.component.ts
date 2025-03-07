import { AfterViewInit, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AlertService } from '../../services/alert.service';
import { UserSession } from '../../utils/UserSession';

@Component({
  selector: 'app-load',
  imports: [],
  templateUrl: './load.component.html',
  styleUrl: './load.component.css'
})
export class LoadComponent implements AfterViewInit {
  constructor (private router: Router, private alert: AlertService){}

  url = "/" + window.location.href.split("/")[window.location.href.split("/").length-1]

  ngAfterViewInit(): void {
    switch (this.url){
      case "/login":
        this.toggleAccess(1)
        break;
        case "/register":
          this.toggleAccess(1)
        break;
        case "/home":
        this.toggleAccess(2)
      break;
      case "/admin":
        this.toggleAccess(3)
      break;
    }
  }
  
  toggleAccess(access_level: number){
    // 0 All
    // 1 Guests only
    // 2 Users and admin
    // 3 Admin only
    // Default all

    switch (access_level){
      case 1:
        if (UserSession.getUser() != "Guest") {
          this.router.navigate(["/home"])
        }
        break;

        case 2:
        if (UserSession.getUser() == "Guest") {
          this.alert.error("Error", "Inicia sesión para ver este contenido")
          this.router.navigate(["/login"])
        }
        break;

        case 3:
        if (UserSession.getRole() != "ROLE_ADMIN") {
          this.alert.error("Error", "No puedes acceder a este contenido")
          this.router.navigate(["/home"])
        }
        break;
      default:
        break;
    }
  }
}
