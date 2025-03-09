import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { AlertService } from '../../services/alert.service';
import { Router } from '@angular/router';
import { UserSession } from '../../utils/UserSession';
import { User } from '../../utils/User';
import { NavComponent } from '../nav/nav.component';


@Component({
  selector: 'app-login',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  constructor(private formBuilder: FormBuilder, private service: UserService, private alert: AlertService, private router: Router) { }

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
            if (data.user.rolesDTO[0].nombre == "ROLE_ADMIN") {
              UserSession.setUser(new User(data.user.id, data.user.username, data.user.email, data.user.pfp, data.user.currency, data.user.rolesDTO, data.user.nextPayment))
              this.router.navigate(['/home'])
            } else {
              const check = document.getElementById("savesession") as HTMLInputElement
              sessionStorage.setItem("saves", check.checked ? "t" : "f")
              if (UserSession.wasLoggedAs(data.user.email)) {
                this.alert.success(data.tit, data.msg)
                  .then((resp) => {
                    if (resp.isDismissed) {
                      window.location.reload()
                    } else {
                      window.location.reload()
                    }
                  })
                UserSession.setUser(new User(data.user.id, data.user.username, data.user.email, data.user.pfp, data.user.currency, data.user.rolesDTO, data.user.nextPayment))
                this.router.navigate(['/home'])
              } else {
                sessionStorage.setItem("aux_user", data.user.email)
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
