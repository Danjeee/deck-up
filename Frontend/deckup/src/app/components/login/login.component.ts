import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';

@Component({
  selector: 'app-login',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  constructor(private formBuilder: FormBuilder){}

  form = this.formBuilder.group({
    email: [
      "",
      [
        Validators.required,
      ],
    ],
    passwd: [
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

  login(){
    if (this.form.valid) {
      console.log("login")
    } else {
      this.form.markAllAsTouched()
    }
  }
}
