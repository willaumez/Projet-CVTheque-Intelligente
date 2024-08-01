import { Component } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { SelectionModel } from '@angular/cdk/collections';

export interface PdfFile {
  name: string;
  dateCreation: string;
  note: number;
  description: string;
}

const PDF_DATA: PdfFile[] = [
  { name: 'Document1', dateCreation: '2023-01-01', note: 5, description: 'Description of Document1' },
  { name: 'Document2', dateCreation: '2023-02-01', note: 4, description: 'Description of Document2' },
  { name: 'Document2', dateCreation: '2023-02-01', note: 4, description: 'Description of Document2' },
  { name: 'Document2', dateCreation: '2023-02-01', note: 4, description: 'Description of Document2' },
  { name: 'Document2', dateCreation: '2023-02-01', note: 4, description: 'Description of Document2' },
  { name: 'Document2', dateCreation: '2023-02-01', note: 4, description: 'Description of Document2' },
  { name: 'Document2', dateCreation: '2023-02-01', note: 4, description: 'Description of Document2' },
  { name: 'Document2', dateCreation: '2023-02-01', note: 4, description: 'Description of Document2' },
  { name: 'Document2', dateCreation: '2023-02-01', note: 4, description: 'Description of Document2' },
  { name: 'Document2', dateCreation: '2023-02-01', note: 4, description: 'Description of Document2' },
  { name: 'Document2', dateCreation: '2023-02-01', note: 4, description: 'Description of Document2' },
  { name: 'Document2', dateCreation: '2023-02-01', note: 4, description: 'Description of Document2' },
  { name: 'Document2', dateCreation: '2023-02-01', note: 4, description: 'Description of Document2' },

  // Ajoutez plus de données si nécessaire
];


@Component({
  selector: 'app-list-view',
  templateUrl: './list-view.component.html',
  styleUrl: './list-view.component.scss'
})
export class ListViewComponent {
  displayedColumns: string[] = ['select', 'name', 'dateCreation', 'note', 'action'];
  dataSource = new MatTableDataSource<PdfFile>(PDF_DATA);
  selection = new SelectionModel<PdfFile>(true, []);

  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  toggleAllRows() {
    if (this.isAllSelected()) {
      this.selection.clear();
      return;
    }

    this.selection.select(...this.dataSource.data);
  }

  /** The label for the checkbox on the passed row */
  checkboxLabel(row?: PdfFile): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.name}`;
  }

  viewDescription(description: string) {
    alert(description);
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  deleteSelection() {

  }

  transferSelection() {

  }



}
