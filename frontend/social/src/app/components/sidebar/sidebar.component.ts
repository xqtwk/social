import {Component, inject, TemplateRef} from '@angular/core';
import {RouterLink, RouterLinkActive} from "@angular/router";
import {
  NgbCollapseModule,
  NgbDropdown,
  NgbDropdownItem,
  NgbDropdownMenu,
  NgbDropdownToggle,
  NgbNav,
  NgbDatepickerModule,
  NgbOffcanvas
} from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [
    RouterLink,
    NgbDropdown,
    NgbDropdownMenu,
    NgbDropdownItem,
    NgbDropdownToggle,
    NgbNav,
    RouterLinkActive,
    NgbCollapseModule,
    NgbNav,
    NgbDatepickerModule
  ],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss'
})
export class SidebarComponent {
  isMenuCollapsed = true;
  private canvasService = inject(NgbOffcanvas);

  constructor() {
  }

  openCanvas(content: TemplateRef<any>) {
    this.canvasService.open(content, {ariaLabelledBy: 'canvasMenuTitle'}).result.then(
      (result) => {
        console.log(`Closed with: ${result}`);
      },
      (reason) => {
        console.log(`Dismissed ${this.getDismissReason(reason)}`);
      }
    );
  }

  private getDismissReason(reason: any): string {
    if (reason === 'ESC') {
      return 'by pressing ESC';
    } else if (reason === 'BACKDROP_CLICK') {
      return 'by clicking on the backdrop';
    } else {
      return `with: ${reason}`;
    }
  }
}
