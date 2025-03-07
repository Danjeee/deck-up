import { Injectable } from '@angular/core';
import Swal, { SweetAlertResult } from 'sweetalert2';

@Injectable({
  providedIn: 'root'
})
export class AlertService {
 

  constructor() { }

  success(title: string, text: string): Promise<SweetAlertResult<any>>{
    return Swal.fire({
      title: title,
      text: text,
      showCloseButton: true,
      icon: 'success',
    })
  }
  error(title: string, text: string): Promise<SweetAlertResult<any>>{
    return Swal.fire({
      title: title,
      text: text,
      showCloseButton: true,
      icon: 'error'
    })
  }
  ask(title: string, text: string) {
    return Swal.fire({
      title: title,
      text: text,
      input: 'text',
      showCloseButton: false,
      icon: 'question'
    })
  }
}
