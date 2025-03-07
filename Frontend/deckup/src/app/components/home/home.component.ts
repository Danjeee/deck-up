import { Component } from '@angular/core';
import { UserSession } from '../../utils/UserSession';

@Component({
  selector: 'app-home',
  imports: [],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {

  usuario = UserSession.getUser()

}
