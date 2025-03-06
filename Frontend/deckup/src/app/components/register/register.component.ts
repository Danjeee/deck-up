import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { AlertService } from '../../services/alert.service';
import { Router } from '@angular/router';
import { UserSession } from '../../utils/UserSession';
import { User } from '../../utils/User';

@Component({
  selector: 'app-register',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  constructor(private formBuilder: FormBuilder, private service: UserService, private alert: AlertService, private router: Router) { }

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

  changeImg(): void{
    const imagen = document.getElementById("img") as HTMLImageElement
    const input = document.getElementById("pfp_inp") as HTMLInputElement
    const files: any = input.files
    imagen.src = window.URL.createObjectURL(files[0])
  }

  register() {
    if (this.form.valid) {
      const formdata = new FormData(document.getElementById("form") as HTMLFormElement)
      this.service.login(formdata).subscribe({
        next: (data) => {
          if (data.status == 200) {
            this.alert.success(data.tit, data.msg)
            UserSession.setUser(new User(data.user.id, data.user.username, data.user.email, data.user.pfp, data.user.currency, data.user.rolesDTO))
            this.router.navigate(['/home'])
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
