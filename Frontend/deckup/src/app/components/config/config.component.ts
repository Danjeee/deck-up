import { Component, OnInit } from '@angular/core';
import { environmentsURLs } from '../../utils/environmentsURls';
import { UserSession } from '../../utils/UserSession';
import Swal from 'sweetalert2';
import { va } from '../../utils/Utils';
import { UserService } from '../../services/user.service';
import { User } from '../../utils/User';
import { AlertService } from '../../services/alert.service';
import { ApacheService } from '../../services/apache.service';

@Component({
  selector: 'app-config',
  imports: [],
  templateUrl: './config.component.html',
  styleUrl: './config.component.css'
})
export class ConfigComponent extends environmentsURLs implements OnInit {

  user: any

  constructor(private service: UserService, private alert: AlertService, private resService: ApacheService) {
    super()
  }
  ngOnInit(): void {
    this.user = UserSession.getUser()
  }

  chguser() {
    Swal.fire({
      icon: "question",
      title: "Cambio de nombre",
      text: "¿Cual quieres que sea tu nombre de usuario?",
      input: "text",
      customClass: {
        popup: "swal-drk btn skew",
        title: "swal-drk",
        confirmButton: "btn but str swal-btn"
      }
    }).then((res) => {
      if (!res.isDismissed) {
        if (va(res.value)) {
          this.service.changeUsername(res.value).subscribe({
            next: (data: any) => {
              if (data.status == 200) {
                let aux: User = UserSession.getUser()
                aux.username = res.value;
                UserSession.setUser(aux, true)
                this.alert.success("Nombre cambiado", "Bienvenid@ " + data.msg)
              } else {
                this.alert.error(data.tit, data.msg)
              }
            }
          })
        }
      }
    })
  }

  upload() {
    const body: FormData = new FormData()
    const input = document.getElementById("pfp_input") as HTMLInputElement
    const prev = UserSession.getUser().pfp
    let files
    if (input.files == null) {
      this.alert.error("Error", "No has seleccionado un archivo")
      return
    } else {
      files = input.files[0]
    }
    body.append("opc", "ADD")
    body.append("pfp", files)
    this.resService.uploadUserImg(body).subscribe({
      next: (data) => {
        this.service.changePFP(UserSession.getUser().auth, data.name).subscribe({
          next: (res: any) => {
            const newbody: FormData = new FormData()
            newbody.append("opc", "DEL")
            if (res.status == 200) {
              this.alert.success("¡Hecho!", "Imagen cambiada con éxito")
              let user = UserSession.getUser()
              user.pfp = data.name
              UserSession.setUser(user, true);
              (document.getElementById("pfp_aux") as HTMLImageElement).src = UserSession.generateImgRoute();
              newbody.append("name", prev)
            } else {
              this.alert.error(res.tit,res.msg)
              newbody.append("name", data.name)
            }
            this.resService.uploadUserImg(newbody).subscribe({
              next: (data) => {}
            })
          }
        })
      }
    })
  }
  upload_p1() {
    (document.getElementById("pfp_input") as HTMLElement).click()
  }



}
