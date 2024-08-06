import { Component } from '@angular/core';
import {
  CdkDragDrop,
  moveItemInArray,
  transferArrayItem,
} from '@angular/cdk/drag-drop';

@Component({
  selector: 'app-files-view',
  templateUrl: './files-view.component.html',
  styleUrl: './files-view.component.scss'
})
export class FilesViewComponent {
  isListView: boolean= true;
  isGridView: boolean= false;

  toggleListView() {
    this.isListView = true;
    this.isGridView = false;

  }
  toggleGridView() {
    this.isGridView = true;
    this.isListView = false;
  }

  ///
  items = ['Carrots', 'Tomatoes', 'Onions', 'Apples'];

  basket = ['Oranges', 'Bananas', 'Cucumbers', 'Avocados'];

  drop(event: CdkDragDrop<string[]>) {
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex,
      );
    }
  }


}
