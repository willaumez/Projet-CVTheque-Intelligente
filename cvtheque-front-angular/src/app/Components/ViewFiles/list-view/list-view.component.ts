import {
  AfterViewChecked,
  Component,
  inject,
  Input,
  OnChanges,
  OnInit,
  SimpleChanges,
  ViewChild
} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {MatPaginator, MatPaginatorModule} from '@angular/material/paginator';
import {FilesService} from "../../../Services/FileServices/files.service";
import {FileDB, ResultCriteria, ResultKeywords} from "../../../Models/FileDB";
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
export class ListViewComponent implements OnInit, OnChanges, AfterViewChecked  {
  isLoading = false;
  inOperation: boolean = false;
  error: string = '';

  displayedColumns: string[] = ['select', 'name', 'type', 'folder', 'dateCreation', 'results', 'action'];
  dataSource = new MatTableDataSource<FileDB>();
  selection: number[] = [];
  dataLoaded: boolean = false;

  @ViewChild(MatPaginator, { static: false }) paginator!: MatPaginator;

  @Input() data: FileDB[] | null = [];
  @Input() folderId!: number;
  isResult: boolean = false;
  resultData: ResultCriteria | undefined;
  resultKeyword: ResultKeywords | undefined;
  pdfSrc = '';


  ngOnChanges(changes: SimpleChanges) {
    if (changes['data'].currentValue.length>0) {
      //console.log('Data received in app-list-view : ', changes['data']);
      // @ts-ignore
      this.dataSource.data = this.data;
      //this.sortDataByAcceptCriteriaLength();
      if (this.data?.some(item => item.acceptCriteria && item.acceptCriteria.length > 0)) {
        this.sortData('acceptCriteria');
        console.log('Sorting by acceptCriteria');
      } else if (this.data?.some(item => item.existWords && item.existWords.length > 0)) {
        this.sortData('existWords');
        console.log('Sorting by existWords');
      } else {
        console.warn('No valid criteria found for sorting.');
      }
      this.dataLoaded = true;
      //console.log('Données reçues dans app-list-view : ', JSON.stringify(this.data, null, 2));
    }else {
      this.getAllFiles();
    }
  }

  ngOnInit(): void {
    if (!this.dataLoaded) {
        this.getAllFiles();
    }
  }

  constructor(private _fileService: FilesService, private _dialog: MatDialog) {
  }

  ngAfterViewChecked() {
    if (this.paginator && this.dataSource.paginator !== this.paginator) {
      this.dataSource.paginator = this.paginator;
    }
  }



// Fonction de tri personnalisée
  sortData(criteriaType: 'acceptCriteria' | 'existWords') {
    this.dataSource.data = this.dataSource.data.sort((a, b) => {
      const aLength = criteriaType === 'acceptCriteria' ? (a.acceptCriteria?.length || 0) : (a.existWords?.length || 0);
      const bLength = criteriaType === 'acceptCriteria' ? (b.acceptCriteria?.length || 0) : (b.existWords?.length || 0);
      return bLength - aLength;
    });
  }

  getAllFiles() {
    this.isLoading = true;

    // Vérifier si folderId existe et appeler la méthode appropriée dans le service
    if (this.folderId !== undefined) {
      this._fileService.getAllFiles(this.folderId).subscribe(
        (files: FileDB[]) => {
          this.dataSource.data = files;
          this.isLoading = false;
        },
        (error) => {
          this.error = 'Error fetching files: ' + error.message;
          this.isLoading = false;
        }
      );
    } else {
      this._fileService.getAllFiles().subscribe(
        (files: FileDB[]) => {
          this.dataSource.data = files;
          this.isLoading = false;
        },
        (error) => {
          this.error = 'Error fetching files: ' + error.message;
          this.isLoading = false;
        }
      );
    }
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
    let conf: boolean = confirm("Are you sure to delete the selected items?");
    if (!conf) return;

    this.inOperation = true; // Début de l'opération

    if (this.selection.length > 0) {
      this._fileService.deleteFiles(this.selection).subscribe(
        (response) => {
          this.selection = [];
          this.getAllFiles();
          this.inOperation = false;
        },
        (error) => {
          this.error = 'Error Deleting Files: ' + error.message;
          this.inOperation = false;
        }
      );
    } else {
      this.inOperation = false;
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
          //console.log('Files transferred to:', val);
          this.selection = [];
          this.getAllFiles();
        }
      },
      error: (error) => {
        this.error = 'Error transferring files: ' + error.message;
      }
    });
  }


  criteriaInfos(file: FileDB) {
    this.isResult = true;
    this.viewFile(file.id);
    this.resultData = {
      acceptCriteria: file.acceptCriteria,
      rejectCriteria: file.rejectCriteria
    }
  }
  keywordsInfos(file: FileDB) {
    this.isResult = true;
    this.viewFile(file.id);
    this.resultKeyword = {
      existWords: file.existWords,
      noExistWords: file.noExistWords
    }
  }

  returnToFilesView() {
    this.isResult = false;
    this.resultData = undefined;
    this.resultKeyword = undefined;
  }

  //Real File

  viewFile(fileId: any) {
    this._fileService.readFile(fileId).subscribe(
      (response) => {
        let blob: Blob = response.body as Blob;
        let url = window.URL.createObjectURL(blob);
        this.pdfSrc = url;
        //console.log(response);
      });
  }

  calculateAcceptancePercentage(first: any, second: any): string {
    const acceptLength = first || 0;
    const rejectLength = second || 0;
    const total = acceptLength + rejectLength;
    if (total === 0) {
      return '0';
    }
    const percentage = (acceptLength / total) * 100;
    return percentage.toFixed(0);
  }
  getCriteriaColor(first: any, second: any): string {
    const percentage = parseFloat(this.calculateAcceptancePercentage(first, second));
    return percentage >= 50 ? 'green' : 'red';
  }


}
