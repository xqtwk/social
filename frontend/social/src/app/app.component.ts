import { Component } from '@angular/core';
import {RouterLink, RouterOutlet} from '@angular/router';
import {SidebarComponent} from "./pages/sidebar/sidebar.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, SidebarComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
}
