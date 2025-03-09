import { AfterViewInit, Component, OnInit } from '@angular/core';
import { UserSession } from '../../utils/UserSession';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home',
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  ngOnInit(): void {
    setTimeout(() => {
      const user_np = new Date(UserSession.getUser().pay)
    if (user_np < new Date()) {
      this.can_pay = true
    } else {
      this.can_pay = false
    }
    }, 1);
  }

  usuario = UserSession.getUser()

  time_left: Date = new Date()

  can_pay: boolean = false

}
