import {ChangeDetectorRef, Component, Input, OnInit} from '@angular/core';
import {SelectionModel} from "@angular/cdk/collections";
import {FileDB, ResultCriteria, ResultKeywords, Scoring} from "../../../Models/FileDB";
import {FilesService} from "../../../Services/FileServices/files.service";
import {AddFolderComponent} from "../../Dialog/add-folder/add-folder.component";
import {MatDialog} from "@angular/material/dialog";
import {PdfViewerComponent} from "../../Dialog/pdf-viewer/pdf-viewer.component";
import {EvaluationDto} from "../../../Models/EvaluationDto";

@Component({
  selector: 'app-grid-view',
  templateUrl: './grid-view.component.html',
  styleUrl: './grid-view.component.scss'
})
export class GridViewComponent implements OnInit {
  gridColumns = 10;
  isChecked = false;

  dataSource: FileDB[] = [];
  filteredData = this.dataSource;
  isLoading = false;
  inOperation: boolean = false;
  error: string = '';
  pdfSrc = '';

  resultData: ResultCriteria | undefined;
  resultKeyword: ResultKeywords | undefined;

  selection = new SelectionModel<number>(true, []);

  @Input() folderId!: number;
  isResult: boolean = false;
  dataLoaded: boolean = false;

  ngOnInit(): void {
    if (!this.dataLoaded) {
      this.getAllFiles();
    }
  }
  refreshData() {
    this.getAllFiles();
  }

  constructor(private _fileService: FilesService, private _dialog: MatDialog, private cdr: ChangeDetectorRef) {
  }

  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.filteredData.length;
    return numSelected === numRows;
  }

  checkboxLabel(row?: number): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row}`;
  }

  toggleSelection(fileId: number) {
    this.selection.toggle(fileId);
  }

  toggleAllGrid() {
    if (this.isAllSelected()) {
      this.selection.clear();
      return;
    }
    this.selection.select(...this.filteredData.map(num => num.id));
  }
  applyFilter(event: KeyboardEvent) {
    const filterValue = (event.target as HTMLInputElement).value.toLowerCase();
    this.filteredData = this.dataSource.filter(file =>
      file.name.toLowerCase().includes(filterValue) ||
      file.folderName.toLowerCase().includes(filterValue) ||
      (file.scoring && file.scoring.score.toString().includes(filterValue)) ||
      (file.existWords && file.existWords.some(word => word.toLowerCase().includes(filterValue))) ||
      (file.noExistWords && file.noExistWords.some(word => word.toLowerCase().includes(filterValue)))
    );
  }

  loadData(){
    this.filteredData = this.dataSource;
  }

  deleteSelection() {
    let conf: boolean = confirm("Are you sure to delete the selected items?");
    if (!conf) return;
    this.inOperation = true;
    if (this.selection.selected.length > 0) {
      this._fileService.deleteFiles(this.selection.selected).subscribe({
        next: (response) => {
          this.selection.clear();
          this.getAllFiles();
          this.inOperation = false;
        },
        error: (error) => {
          this.error = 'Error deleting files: ' + error.message;
          this.inOperation = false;
        }
      });
    } else {
      this.inOperation = false;
    }
  }

  transferSelection() {
    const data = this.selection.selected;
    const dialogRef = this._dialog.open(AddFolderComponent, {
      data,
    });
    dialogRef.afterClosed().subscribe({
      next: (val) => {
        if (val) {
          this.selection.clear()
          this.getAllFiles();
        }
      },
      error: (error) => {
        this.error = 'Error transferring files: ' + error.message;
      }
    });
  }

  //-----------------------------------------------------------------------
  getAllFiles() {
    this.isLoading = true;
    if (this.folderId !== undefined) {
      this._fileService.getAllFiles(this.folderId).subscribe({
        next: (files: FileDB[]) => {
          this.dataSource = files;
          this.loadData();
          this.isLoading = false;
        },
        error: (error) => {
          this.error = 'Error fetching files: ' + error.message;
          this.isLoading = false;
        }
      });
    } else {
      this._fileService.getAllFiles().subscribe({
        next: (files: FileDB[]) => {
          this.dataSource = files;
          this.loadData();
          this.isLoading = false;
        },
        error: (error) => {
          this.error = 'Error fetching files: ' + error.message;
          this.isLoading = false;
        }
      });
    }
  }

  viewFile(fileId: any) {
    this._fileService.readFile(fileId).subscribe(
      (response) => {
        let blob: Blob = response.body as Blob;
        this.pdfSrc = window.URL.createObjectURL(blob);
      });
  }
  //Evaluation Infos:
  evaluation!: EvaluationDto;
  evaluationInfos(file: number) {
    this.isLoading = true;
    this._fileService.getEvaluationInfos(file).subscribe({
      next: (response: EvaluationDto) => {
        this.viewFile(file);
        this.evaluation = response;
        this.isLoading = false;
      },
      error: (error) => {
        this.error = 'Error fetching evaluation infos: ' + error.message;
      }
    });
    this.isResult = true;
  }


  returnToFilesView() {
    this.isResult = false;
    this.resultData = undefined;
    this.resultKeyword = undefined;
  }

  //Real File
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
        if (val && this.dataLoaded) {
          this.getAllFiles();
        }
      },
    });
  }

  checkedClick() {
    if (this.isChecked) {
      this.selection.clear();
    }
  }

  deleteEvaluation(id: number) {
    let conf: boolean = confirm("Are you sure you want to delete the evaluation?");
    if (!conf) return;
    this.isLoading = true;
    this._fileService.deleteEvaluation(id).subscribe({
      next: () => {
        console.log('Evaluation deleted successfully:');
        this.isLoading = false;
        this.isResult = false;
        this.getAllFiles();
      },
      error: (error) => {
        this.error = 'Error deleting evaluation: ' + (error.message || 'Unknown error');
        this.isLoading = false;
      }
    });
  }
  deleteAllEvaluation() {
    let conf: boolean = confirm("Are you sure to delete the selected evaluations?");
    if (!conf) return;
    this.inOperation = true; // Début de l'opération
    if (this.selection.selected.length > 0) {
      this._fileService.deleteAllEvaluation(this.selection.selected).subscribe({
        next: (response) => {
          this.selection.clear();
          this.getAllFiles();
          this.inOperation = false;
        },
        error: (error) => {
          this.error = 'Error deleting files: ' + error.message;
          this.inOperation = false;
        }
      });
    } else {
      this.inOperation = false;
    }
  }



}
