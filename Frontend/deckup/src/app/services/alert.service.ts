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
        popup: "swal-c1 swal-drk btn skew",
        title: "swal-drk",
        confirmButton: "btn but str swal-btn"
      }
    })
  }
  carta_recibida(carta: any, cant: number){
    return Swal.fire({
      title: "¡Enhorabuena!",
      text: "¡Has recibido "+cant +" "+carta.nombre +"!",
      showCloseButton: true,
      iconHtml: '<i class="bi bi-gem swal-gm"></i>',
      customClass: {
        popup: "swal-c1 swal-drk btn skew",
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
        popup: "swal-drk btn skew",
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
        popup: "swal-drk btn skew",
        title: "swal-drk",
        confirmButton: "btn but str swal-btn"
      }
    })
  }
  ask(title: string, text: string, canempty: boolean = false, icon: any = 'question') {
    return Swal.fire({
      title: title,
      text: text,
      input: 'text',
      showCloseButton: false,
      icon: icon,
      customClass: {
        popup: "swal-drk btn skew",
        title: "swal-drk",
        confirmButton: "btn but str swal-btn"
      },
      preConfirm: (value) => {
        if (!value && !canempty) {
          Swal.showValidationMessage('<i class="fa fa-info-circle"></i> No puedes dejar este campo vacío')
        }
      },
    })
  }
  confirm(title: string, text: string, ifConfirmed: () => void, icon?: any, ifNot?: () => void ){
    Swal.fire({
          title: title,
          text: text,
          icon: icon ? icon : "warning",
          confirmButtonText: 'Confirmar',
          showCancelButton: true,
          cancelButtonText: "Cancelar",
          cancelButtonColor: "#DC3545",
          customClass: {
            popup: "swal-drk btn skew",
            title: "swal-drk",
            confirmButton: "btn but str swal-btn",
            cancelButton: "btn but str swal-btn",
          }
        }).then(result => {
          if (result.isConfirmed) {
            ifConfirmed()
          } else {
            ifNot ? ifNot() : {}
          }
        })
  }
}
