import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { AlertService } from '../../services/alert.service';
import { Router } from '@angular/router';
import { UserSession } from '../../utils/UserSession';
import { User } from '../../utils/User';
import { ApacheService } from '../../services/apache.service';
import { environmentsURLs } from '../../utils/environmentsURls';

@Component({
  selector: 'app-register',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent extends environmentsURLs {
  constructor(private formBuilder: FormBuilder, private service: UserService, private alert: AlertService, private router: Router, private apacheService: ApacheService) {
    super()
   }

  auth: string | null = null

  auth_encoded: string | null = null

  loading: boolean = false

  fileName: string = ""

  form = this.formBuilder.group({
    email: [
      "",
      [
        Validators.required,
      ],
    ],
    username: [
      "",
      [
        Validators.required,
      ],
    ],
    passwordc: [
      "",
      [
        Validators.required, this.passwordMatchValidator.bind(this)
      ],
    ],
    password: [
      "",
      [
        Validators.required, Validators.maxLength(30), Validators.minLength(6)
      ],
    ],
    pfp: []
  })

  passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const pass = this.form?.get("password")?.value;
    const rep = control.value;
    return pass === rep ? null : { eq: true };
  }

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

  changeImg(): void {
    const imagen = document.getElementById("img") as HTMLImageElement
    const input = document.getElementById("pfp_inp") as HTMLInputElement
    const files: any = input.files
    imagen.src = window.URL.createObjectURL(files[0])
  }

  register() {
    if (this.form.valid) {
      const formdata = new FormData(document.getElementById("form") as HTMLFormElement)
      if (this.auth != null) {
        formdata.append("auth", this.auth_encoded as string)
      }
      setTimeout(() => {
        this.loading = true
        this.service.register(formdata, this.auth as string).subscribe({
          next: (data) => {
            this.loading = false
            if (data.status == 100) {
              this.auth_encoded = data.msg
              this.alert.ask("Codigo de verificacion", "Inserta el codigo de verificacion que te hemos mandado al correo: " + formdata.get("email")).then((result) => {
                var resp: any = result
                this.auth = resp.value;
                if (resp.value == null || resp.value == "") {
                  this.auth = "w"
                }
                this.register()
              })
            } else if (data.status == 200) {
              UserSession.setUser(new User(data.user.id, data.user.username, data.user.email, data.user.pfp, data.user.currency, data.user.rolesDTO, data.user.nextPayment, data.user.auth))
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
              this.auth = null
            }
          }
        })
      }, 200);
    } else {
      this.alert.error("Error", "Rellena todos los campos")
      this.form.markAllAsTouched()
    }
  }
}
