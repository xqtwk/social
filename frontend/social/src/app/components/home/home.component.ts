import { Component } from '@angular/core';
import {RouterLink} from "@angular/router";
import {CreatePostComponent} from "../create-post/create-post.component";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    RouterLink,
    CreatePostComponent
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {

}
