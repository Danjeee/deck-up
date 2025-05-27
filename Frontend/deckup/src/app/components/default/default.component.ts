import { Component, OnInit } from '@angular/core';
import { environmentsURLs } from '../../utils/environmentsURls';
import { Router } from '@angular/router';
import { generateHeader } from '../../utils/Utils';

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
  }
}
