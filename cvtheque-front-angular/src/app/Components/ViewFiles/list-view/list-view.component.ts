import {Component, OnInit} from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { SelectionModel } from '@angular/cdk/collections';
import {FilesService} from "../../../Services/FileServices/files.service";
import {FileDB} from "../../../Models/FileDB";
import {MatDialog} from "@angular/material/dialog";
import {PdfViewerComponent} from "../../Dialog/pdf-viewer/pdf-viewer.component";
import {AddFolderComponent} from "../../Dialog/add-folder/add-folder.component";

export interface PdfFile {
  name: string;
  dateCreation: string;
  note: number;
  description: string;
}

@Component({
  selector: 'app-list-view',
  templateUrl: './list-view.component.html',
  styleUrl: './list-view.component.scss'
})
export class ListViewComponent implements OnInit {
  isLoading = false;
  error: string = '';

  displayedColumns: string[] = ['select', 'name', 'type','folder', 'dateCreation', 'action'];
  dataSource = new MatTableDataSource<FileDB>();
  selection: number[] = [];

  pdfSrc = '';

  ngOnInit(): void {
    this.getAllFiles();
  }

  constructor(private _fileService: FilesService, private _dialog: MatDialog) {}

  getAllFiles() {
    this.isLoading = true;
    this._fileService.getAllFiles().subscribe(
      (files: FileDB[]) => {
        this.dataSource.data = files;
        this.isLoading = false;
      },
      (error) => {
        this.error = 'Error fetching files: ' + error;
        this.isLoading = false;
      }
    );
  }

  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    const numSelected = this.selection.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  toggleAllRows() {
    if (this.isAllSelected()) {
      this.selection = [];
    } else {
      this.selection = this.dataSource.data.map(row => row.id);
    }
  }

  /** The label for the checkbox on the passed row */
  checkboxLabel(row?: FileDB): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.includes(row.id) ? 'deselect' : 'select'} row ${row.name}`;
  }

  /** Toggles the selection of a single row */
  toggleRow(row: FileDB) {
    const index = this.selection.indexOf(row.id);
    if (index >= 0) {
      this.selection.splice(index, 1);
    } else {
      this.selection.push(row.id);
    }
  }

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
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  deleteSelection() {
    let conf: boolean = confirm("Are you sure to delete the selected items?")
    if (!conf) return;

    if (this.selection.length > 0) {
      this._fileService.deleteFiles(this.selection).subscribe(
        (response) => {
          console.log("Files Deleted Successfully");
          this.selection = [];
          this.getAllFiles();
        },
        (error) => {
          console.log("Error Deleting Files");
        }
      );
    }
  }


  transferSelection() {
    const data = this.selection;
    const dialogRef = this._dialog.open(AddFolderComponent, {
      data,
    });
    dialogRef.afterClosed().subscribe({
      next: (val) => {
        if (val) {
          console.log('Files transferred to:', val);
          this.getAllFiles();
        }
      },
    });
  }

}
