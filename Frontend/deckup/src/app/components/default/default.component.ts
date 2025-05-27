import { Component, OnInit } from '@angular/core';
import { environmentsURLs } from '../../utils/environmentsURls';
import { Router } from '@angular/router';
import { generateHeader, $$, DOM, Localizer } from '../../utils/Utils';

@Component({
  selector: 'app-default',
  imports: [],
  templateUrl: './default.component.html',
  styleUrl: './default.component.css'
})
export class DefaultComponent extends environmentsURLs implements OnInit {

  constructor(private router:Router){
    super()
  }

  ngOnInit(): void {
    generateHeader({
      height: "12dvh",
      background: "#13253e",
      title:"DeckUp",
      customClass: "str",
    })
    const l: Localizer = new Localizer()
    l.set({
      hola: "Hola"
    })
    l.addLngs({
      BR: "Brasil"
    })
    l.add({
      gracias: "Obrigado"
    }, "BR")
    console.log(l.get("graCias", "BR", "test"))
  }
}
