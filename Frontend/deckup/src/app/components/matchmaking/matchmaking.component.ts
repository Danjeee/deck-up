import { CommonModule } from '@angular/common';
import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core';
import { MatchmakingService } from '../../services/matchmaking.service';
import { ParticleComponent } from '../particle/particle.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-matchmaking',
  imports: [CommonModule],
  templateUrl: './matchmaking.component.html',
  styleUrl: './matchmaking.component.css'
})
export class MatchmakingComponent implements AfterViewInit, OnDestroy {


  loading: boolean = true;

  constructor(private service: MatchmakingService, private router: Router) { }

  ngAfterViewInit(): void {
    
    this.listener() 
    
    setTimeout(() => { // Evitar errores y mejorar matchmaking ademas de dar tiempo a cancelar
      if (this.router.url == "/matchmaking") {
        this.service.startMatch().subscribe()
      }
    }, 3000);
  }

  listener() {
    this.service.joinListener()

    this.service.getstatus().subscribe((accepted: any) => {
      if (accepted != "" && accepted != null) {
        setTimeout(() => {
          sessionStorage.setItem("game", accepted)
          this.router.navigate(['/game'])
        }, 500);
        ParticleComponent.popupMsg("Partida encontrada")
      }
    })
  }

  ngOnDestroy(): void {
    this.service.disconnect()
    if (this.router.url != "/game"){
      this.service.cancel().subscribe({
        next: (data) => {console.log(data)}
      })
    }
  }

  back(){
    this.router.navigate(["/home"])
  }
}
