import {Component, ElementRef, ViewChild} from '@angular/core';

@Component({
  selector: 'app-create-post',
  standalone: true,
  imports: [],
  templateUrl: './create-post.component.html',
  styleUrl: './create-post.component.css'
})
export class CreatePostComponent {
  @ViewChild('textAreaElement') textAreaElement: ElementRef | undefined;

  constructor() { }

  autoResize() {
    if (this.textAreaElement) { // Check that textAreaElement is not undefined
      const textArea = this.textAreaElement.nativeElement;
      textArea.style.height = 'auto'; // Reset the height to auto
      textArea.style.height = textArea.scrollHeight + 'px'; // Set the height to match the content
    }
  }

}
