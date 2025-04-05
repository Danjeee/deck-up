import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { environmentsURLs } from '../../utils/environmentsURls';
import { ColeccionService } from '../../services/coleccion.service';

@Component({
  selector: 'app-deck-builder',
  imports: [CommonModule],
  templateUrl: './deck-builder.component.html',
  styleUrl: './deck-builder.component.css'
})
export class DeckBuilderComponent extends environmentsURLs implements OnInit {
  playerCards: any
  constructor (private coleccion: ColeccionService){
    super()
  }
  ngOnInit(): void {
    this.coleccion.findAllPlayerCards().subscribe({
      next: (data) => {
        this.playerCards = data
      }
    })
  }


}
