import { Injectable } from '@angular/core';
import Swal, { SweetAlertResult } from 'sweetalert2';

@Injectable({
  providedIn: 'root'
})
export class AlertService {
 

  constructor() { }


  pago_recibido(amount: number){
    return Swal.fire({
      title: "¡Enhorabuena!",
      text: "¡Has recibido "+amount +" gemas!",
      showCloseButton: true,
      iconHtml: '<i class="bi bi-gem swal-gm"></i>',
      customClass: {
        popup: "swal-c1 swal-drk btn",
        title: "swal-drk",
        confirmButton: "btn but str swal-btn"
      }
    })
  }
  success(title: string, text: string): Promise<SweetAlertResult<any>>{
    return Swal.fire({
      title: title,
      text: text,
      showCloseButton: true,
      icon: 'success',
      customClass: {
        popup: "swal-drk btn",
        title: "swal-drk",
        confirmButton: "btn but str swal-btn"
      }
    })
  }
  error(title: string, text: string): Promise<SweetAlertResult<any>>{
    return Swal.fire({
      title: title,
      text: text,
      showCloseButton: true,
      icon: 'error',
      customClass: {
        popup: "swal-drk btn",
        title: "swal-drk",
        confirmButton: "btn but str swal-btn"
      }
    })
  }
  ask(title: string, text: string) {
    return Swal.fire({
      title: title,
      text: text,
      input: 'text',
      showCloseButton: false,
      icon: 'question',
      customClass: {
        popup: "swal-drk btn",
        title: "swal-drk",
        confirmButton: "btn but str swal-btn"
      }
    })
  }
}
