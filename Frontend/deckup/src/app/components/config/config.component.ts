import { Component, OnInit } from '@angular/core';
import { environmentsURLs } from '../../utils/environmentsURls';
import { UserSession } from '../../utils/UserSession';
import Swal from 'sweetalert2';
import { va } from '../../utils/Utils';
import { UserService } from '../../services/user.service';
import { User } from '../../utils/User';

@Component({
  selector: 'app-config',
  imports: [],
  templateUrl: './config.component.html',
  styleUrl: './config.component.css'
})
export class ConfigComponent extends environmentsURLs implements OnInit {

  user: any

  constructor(private service: UserService){
    super()
  }
  ngOnInit(): void {
    this.user = UserSession.getUser()
  }

  chguser(){
    Swal.fire({
            icon: "question",
            title: "Oferta",
            text: "¿Cual quieres que sea tu nombre de usuario?",
            customClass: {
              popup: "swal-drk btn skew",
              title: "swal-drk",
              confirmButton: "btn but str swal-btn"
            }
          }).then((res) => {
            if (!res.isDismissed) {
              if (va(res.value)) {
                this.service.changeUsername(res.value).subscribe({
                  next: (data) => {
                    let aux: User = UserSession.getUser()
                    aux.username = res.value;
                    UserSession.setUser(aux)
                  }
                })
              }
            }
          })
  }



}
