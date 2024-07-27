import { Component } from '@angular/core';

@Component({
  selector: 'app-files-view',
  templateUrl: './files-view.component.html',
  styleUrl: './files-view.component.scss'
})
export class FilesViewComponent {
  isListView: boolean= false;
  isGridView: boolean= true;

  toggleListView() {
    this.isListView = true;
    this.isGridView = false;

  }
  toggleGridView() {
    this.isGridView = true;
    this.isListView = false;
  }


}
