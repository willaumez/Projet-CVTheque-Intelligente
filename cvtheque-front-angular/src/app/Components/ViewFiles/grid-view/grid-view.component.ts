import {ChangeDetectorRef, Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {SelectionModel} from "@angular/cdk/collections";
import {PdfFile} from "../list-view/list-view.component";
import {FileDB, ResultCriteria, ResultKeywords, Scoring} from "../../../Models/FileDB";
import {Event} from "@angular/router";
import {FilesService} from "../../../Services/FileServices/files.service";
import {AddFolderComponent} from "../../Dialog/add-folder/add-folder.component";
import {MatDialog} from "@angular/material/dialog";
import {PdfViewerComponent} from "../../Dialog/pdf-viewer/pdf-viewer.component";

@Component({
  selector: 'app-grid-view',
  templateUrl: './grid-view.component.html',
  styleUrl: './grid-view.component.scss'
})
export class GridViewComponent implements OnInit, OnChanges {
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
  resultScoring: Scoring | undefined;

  selection = new SelectionModel<number>(true, []);
  @Input() data!: FileDB[] | null;
  @Input() folderId!: number;
  isResult: boolean = false;
  dataLoaded: boolean = false;

  ngOnInit(): void {
    if (!this.dataLoaded) {
      this.getAllFiles();
      //console.log('GridViewComponent:  ', JSON.stringify(this.dataSource, null, 2));
    }
  }

  constructor(private _fileService: FilesService, private _dialog: MatDialog, private cdr: ChangeDetectorRef) {
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['data'].currentValue.length > 0) {
      // @ts-ignore
      this.dataSource = this.data;

      this.returnToFilesView();
      //this.sortDataByAcceptCriteriaLength();
      if (this.data?.some(item => item.acceptCriteria && item.acceptCriteria.length > 0)) {
        this.sortData('acceptCriteria');
      } else if (this.data?.some(item => item.existWords && item.existWords.length > 0)) {
        this.sortData('existWords');
      } else if (this.data?.some(item => item.scoring)) {
        this.sortData('scoring');
      } else {
        this.error = ('No valid criteria found for sorting.');
      }
      this.loadData();
      this.dataLoaded = true;
    } else {
      this.getAllFiles();
    }
  }

  // Fonction de tri personnalisée
  sortData(criteriaType: 'acceptCriteria' | 'existWords' | 'scoring') {
    this.dataSource = this.dataSource.sort((a, b) => {
      let aValue = 0;
      let bValue = 0;

      if (criteriaType === 'acceptCriteria') {
        aValue = a.acceptCriteria?.length || 0;
        bValue = b.acceptCriteria?.length || 0;
      } else if (criteriaType === 'existWords') {
        aValue = a.existWords?.length || 0;
        bValue = b.existWords?.length || 0;
      } else if (criteriaType === 'scoring') {
        aValue = a.scoring?.score || 0;
        bValue = b.scoring?.score || 0;
      }
      return bValue - aValue;
    });
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
    //console.log(this.selection.selected);
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

    this.inOperation = true; // Début de l'opération

    if (this.selection.selected.length > 0) {
      this._fileService.deleteFiles(this.selection.selected).subscribe(
        (response) => {
          this.selection.clear();
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
    const data = this.selection.selected;
    const dialogRef = this._dialog.open(AddFolderComponent, {
      data,
    });
    dialogRef.afterClosed().subscribe({
      next: (val) => {
        if (val) {
          //console.log('Files transferred to:', val);
          //this.dataSource = [];
          this.selection.clear()
          this.getAllFiles();
        }
      },
      error: (error) => {
        this.error = 'Error transferring files: ' + error.message;
      }
    });
    //this.getAllFiles();
  }

  //-----------------------------------------------------------------------
  getAllFiles() {
    this.isLoading = true;

    // Vérifier si folderId existe et appeler la méthode appropriée dans le service
    if (this.folderId !== undefined) {
      this._fileService.getAllFiles(this.folderId).subscribe(
        (files: FileDB[]) => {
          this.dataSource = files;
          this.loadData();
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
          this.dataSource = files;
          this.loadData();
          this.isLoading = false;
        },
        (error) => {
          this.error = 'Error fetching files: ' + error.message;
          this.isLoading = false;
        }
      );
    }
  }

  viewFile(fileId: any) {
    this._fileService.readFile(fileId).subscribe(
      (response) => {
        let blob: Blob = response.body as Blob;
        let url = window.URL.createObjectURL(blob);
        this.pdfSrc = url;
        //console.log(response);
      });
  }

  criteriaInfos(file: FileDB) {
    this.isResult = true;
    this.loadData();
    this.viewFile(file.id);
    this.resultData = {
      acceptCriteria: file.acceptCriteria,
      rejectCriteria: file.rejectCriteria
    }
  }

  keywordsInfos(file: FileDB) {
    this.isResult = true;
    this.loadData();
    this.viewFile(file.id);
    this.resultKeyword = {
      existWords: file.existWords,
      noExistWords: file.noExistWords
    }
  }
  scoringInfos(fileId: number, scoring: Scoring) {
    this.isResult = true;
    this.loadData();
    this.viewFile(fileId);
    this.resultScoring = scoring
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
