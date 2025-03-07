import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavComponent } from "./components/nav/nav.component";
import { LoadComponent } from "./components/load/load.component";

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavComponent, LoadComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'deckup';
}