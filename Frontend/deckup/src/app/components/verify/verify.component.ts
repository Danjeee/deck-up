import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { UserService } from '../../services/user.service';
import { AlertService } from '../../services/alert.service';
import { UserSession } from '../../utils/UserSession';
import { Router } from '@angular/router';
import { User } from '../../utils/User';

@Component({
  selector: 'app-verify',
  imports: [CommonModule],
  templateUrl: './verify.component.html',
  styleUrl: './verify.component.css'
})
export class VerifyComponent implements OnInit {

  constructor(private service: UserService, private alert: AlertService, private router: Router) { }

  loading: boolean = true

  mail: string = sessionStorage.getItem("aux_user") as string

  ngOnInit(): void {
    if (sessionStorage.getItem("aux_user") == null) {
      this.router.navigate([UserSession.getUser() == "Guest" ? "/login" : "/home"])
    }
    const input = document.getElementById("verify") as HTMLInputElement
    input.addEventListener('keyup', ()=>{
      input.value = input.value.toUpperCase()
    })
    this.service.addVerification(sessionStorage.getItem("aux_user") as string).subscribe({
      next: (data) => {
        //console.log(data)
        this.loading = false
        this.countdown_func()
      }
    })
    
  }

  check() {
    const input = document.getElementById("verify") as HTMLInputElement
    const data: FormData = new FormData()
    data.append("auth", input.value.toUpperCase())
    data.append("email", sessionStorage.getItem("aux_user") as string)
    data.append("password", sessionStorage.getItem("aux_pass") as string)
    if (input.value != "" && input.value != null) {
      this.service.verify(data).subscribe({
        next: (data) => {
          if (data.status == 200) {
            UserSession.setUser(new User(data.user.id, data.user.username, data.user.email, data.user.pfp, data.user.currency, data.user.rolesDTO, data.user.nextPayment, data.user.auth))
            if (sessionStorage.getItem("saves") == "t") {
              UserSession.addToPastUsers(sessionStorage.getItem("aux_user") as string)
            }
            sessionStorage.removeItem("aux_user")
            sessionStorage.removeItem("aux_pass")
            sessionStorage.removeItem("saves")
            this.alert.success(data.tit, data.msg)
            .then((resp) => {
              if (resp.isDismissed) {
                window.location.reload()
              } else {
                window.location.reload()
              }
            })
            this.router.navigate(['/home'])
            
          } else {
            this.alert.error(data.tit, data.msg)
          }
        }
      })
    } else {
      this.alert.error("Error", "El codigo no puede estar vacio")
    }
  }

  countdown = 60

  countdown_func() {
    setTimeout(() => {
        this.countdown--
        if (this.countdown > 0) {
          this.countdown_func()
        }
      
    }, 1000);
  }

  resend() {
    this.countdown = 60
    this.loading = true
    this.service.addVerification(sessionStorage.getItem("aux_user") as string).subscribe({
      next: (data) => {
        //console.log(data)
        this.loading = false
        this.countdown_func()
      }
    })
    
  }

}
