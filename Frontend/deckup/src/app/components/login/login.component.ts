import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { AlertService } from '../../services/alert.service';
import { Router } from '@angular/router';
import { UserSession } from '../../utils/UserSession';
import { User } from '../../utils/User';
import { NavComponent } from '../nav/nav.component';
import { ColeccionService } from '../../services/coleccion.service';
import { environmentsURLs } from '../../utils/environmentsURls';
import { css } from '../../utils/Utils';
import { ParticleComponent } from '../particle/particle.component';


@Component({
  selector: 'app-login',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent extends environmentsURLs implements OnInit {


  loaded: boolean = false
  constructor(private formBuilder: FormBuilder, private service: UserService, private alert: AlertService, private router: Router, private cs: ColeccionService) {
    super()
   }
  ngOnInit(): void {
    this.cs.findAllCards().subscribe({
      next: (data) => {
        ParticleComponent.generateDragons(data)
      }
    })
    
  }

  register(){
    this.router.navigate(["register"])
  }

  form = this.formBuilder.group({
    email: [
      "",
      [
        Validators.required,
      ],
    ],
    password: [
      "",
      [
        Validators.required,
      ],
    ],
  })

  errors(field: string): ValidationErrors | null {
    const control = this.form.get(field);
    if (control?.errors) {
      return control.errors;
    }
    return null;
  }

  touched(field: string): boolean | undefined {
    return this.form.get(field)?.touched;
  }

  login() {
    if (this.form.valid) {
      const formdata = new FormData(document.getElementById("form") as HTMLFormElement)
      this.service.login(formdata).subscribe({
        next: (data) => {
          if (data.status == 200) {
            if (data.user.rolesDTO[0].nombre == "ADMIN") {
              UserSession.setUser(new User(data.user.id, data.user.username, data.user.email, data.user.pfp, data.user.currency, data.user.rolesDTO, data.user.nextPayment, data.user.auth))
              this.alert.success(data.tit, data.msg)
              this.router.navigate(['/home'])
            } else {
              const check = document.getElementById("savesession") as HTMLInputElement
              sessionStorage.setItem("saves", check.checked ? "t" : "f")
              if (UserSession.wasLoggedAs(data.user.email)) {
                this.alert.success(data.tit, data.msg)
                UserSession.setUser(new User(data.user.id, data.user.username, data.user.email, data.user.pfp, data.user.currency, data.user.rolesDTO, data.user.nextPayment, data.user.auth))
                this.router.navigate(['/home'])
              } else {
                sessionStorage.setItem("aux_user", data.user.email)
                sessionStorage.setItem("aux_pass", formdata.get("password") as string)
                this.router.navigate(['/verify'])
              }
            }

          } else {
            this.alert.error(data.tit, data.msg)
          }
        }
      })
    } else {
      this.alert.error("Error", "Rellena todos los campos")
      this.form.markAllAsTouched()
    }
  }
}

