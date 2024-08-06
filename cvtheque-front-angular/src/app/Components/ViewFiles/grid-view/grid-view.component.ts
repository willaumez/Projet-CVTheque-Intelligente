import { Component } from '@angular/core';
import {SelectionModel} from "@angular/cdk/collections";
import {PdfFile} from "../list-view/list-view.component";

@Component({
  selector: 'app-grid-view',
  templateUrl: './grid-view.component.html',
  styleUrl: './grid-view.component.scss'
})
export class GridViewComponent {
  gridColumns = 10;
  filesData = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
    2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
    2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
    2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20];
  filteredData = this.filesData;

  selection = new SelectionModel<number>(true, []);

  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.filesData.length;
    return numSelected === numRows;
  }
  calcFlex(gridColumns: number): string {
    return `${100 / gridColumns}%`;
  }

  checkboxLabel(row?: number): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row}`;
  }

  toggleSelection(num: number) {
    this.selection.toggle(num);
    console.log(this.selection.selected);
  }

  toggleAllGrid() {
    if (this.isAllSelected()) {
      this.selection.clear();
      return;
    }else {

    }

    this.selection.select(...this.filesData);
  }

  handleCardClick(num: number) {
    console.log(`Card ${num} clicked`);
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value.toLowerCase();
    this.filteredData = this.filesData.filter(num => num.toString().includes(filterValue));
  }

  deleteSelection() {
    this.filesData = this.filesData.filter(num => !this.selection.isSelected(num));
    this.filteredData = this.filteredData.filter(num => !this.selection.isSelected(num));
    this.selection.clear();
  }

  transferSelection() {
    const selectedItems = this.selection.selected;
    console.log('Transferring selected items:', selectedItems);
    // Ajoutez votre logique de transfert ici
    this.selection.clear();
  }


}
