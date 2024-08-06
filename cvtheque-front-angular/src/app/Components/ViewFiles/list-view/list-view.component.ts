import {Component, OnInit} from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { SelectionModel } from '@angular/cdk/collections';
import {FilesService} from "../../../Services/FileServices/files.service";
import {FileDB} from "../../../Models/FileDB";
import {MatDialog} from "@angular/material/dialog";
import {PdfViewerComponent} from "../../Dialog/pdf-viewer/pdf-viewer.component";

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
export class ListViewComponent implements OnInit {
  isLoading = false;
  error: string = '';

  displayedColumns: string[] = ['select', 'name', 'type', 'dateCreation', 'action'];
  dataSource = new MatTableDataSource<FileDB>();
  selection = new SelectionModel<FileDB>(true, []);

  pdfSrc = '';

  ngOnInit(): void {

    this.getAllFiles()

  }
  constructor(private _fileService: FilesService, private _dialog: MatDialog) {
  }
  getAllFiles() {
    this.isLoading = true;
    this._fileService.getAllFiles().subscribe(
      (files: FileDB[]) => {
        this.dataSource.data = files;
        console.log(files);
      });
    this.isLoading = false;
  }


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
  checkboxLabel(row?: FileDB): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.name}`;
  }

 /* viewDescription(fileId: any) {
    this._fileService.readFile(fileId).subscribe(
      (response) => {
        let blob: Blob = response.body as Blob;
        let url = window.URL.createObjectURL(blob);
        this.pdfSrc= url;
        console.log(response);
      }
    );

    //this.pdfSrc = URL.createObjectURL("http://localhost:8080/file/read");
    //console.log(this.pdfSrc);
  }
  */

  viewDescription(fileId: any) {
    const data: any = fileId;
    const dialogRef = this._dialog.open(PdfViewerComponent, {
      data,
      height: "calc(95% - 20px)",
      minWidth: "calc(55% - 30px)",
      maxWidth: "100%",
      maxHeight: "100%"
    });
    dialogRef.afterClosed().subscribe({
      next: (val) => {
        if (val) {
          this.getAllFiles();
        }
      },
    });

    //this.pdfSrc = URL.createObjectURL("http://localhost:8080/file/read");
    //console.log(this.pdfSrc);
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
