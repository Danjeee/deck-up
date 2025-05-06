import { Component, OnInit } from '@angular/core';
import { NavigationEnd, NavigationStart, Router, RouterOutlet } from '@angular/router';
import { NavComponent } from "./components/nav/nav.component";
import { LoadComponent } from "./components/load/load.component";
import { UserSession } from './utils/UserSession';
import { UserService } from './services/user.service';
import { User } from './utils/User';
import { AlertService } from './services/alert.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavComponent, LoadComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  title = 'deckup';
  
  constructor(private router:Router, private service: UserService, private alert: AlertService){}

  ngOnInit(): void {
      this.router.events.subscribe( e => {
        if (e instanceof NavigationStart) {
          if (UserSession.getUser() != "Guest" && this.router.url != "/login" && this.router.url != "/register") {
            if (sessionStorage.getItem("back") != null){
              LoadComponent.prev.pop()
              sessionStorage.removeItem("back")
            } else {
              if (this.router.url != "deck-builder"){
                LoadComponent.prev.push(this.router.url)
              }
            }
            this.service.restoreUser(UserSession.getId(), UserSession.getUser().auth).subscribe({
              next: (data) => {
                if (data.status == 200) {
                  if (sessionStorage.getItem("game") != null && sessionStorage.getItem("game") != "" && this.router.url != "/game"){
                    this.service.losegame(sessionStorage.getItem("game")).subscribe({next:(data)=>{console.log(this.router.url)}})
                  }
                  UserSession.setUser(new User(data.user.id, data.user.username, data.user.email, data.user.pfp, data.user.currency, data.user.rolesDTO, data.user.nextPayment, data.user.auth))
                } else {
                  this.service.logout()
                  UserSession.logOut()
                  this.alert.error(data.tit, data.msg)
                  this.router.navigate(["/login"])
                }
              }
            })
          }
          
        }
        
      })
  }

}