import {Component, ElementRef, inject, OnInit, ViewChild} from '@angular/core';
import {NgxDropzoneChangeEvent} from "ngx-dropzone";
import {FormControl, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {Observable} from 'rxjs';
import {map, startWith} from 'rxjs/operators';
import {MatDialog} from "@angular/material/dialog";
import {AddFolderComponent} from "../Dialog/add-folder/add-folder.component";



@Component({
  selector: 'app-load-files',
  templateUrl: './load-files.component.html',
  styleUrl: './load-files.component.scss'
})
export class LoadFilesComponent implements OnInit {
  //progress


  //
  files: File[] = [];

  folderCtrl = new FormControl('');
  filteredFolders: Observable<string[]>;
  folders: string[] = [
    'Folder1', 'Folder2', 'Folder3', 'Folder4', 'Folder5', 'Folder6', 'Folder7', 'Folder8', 'Folder9', 'Folder10',
    'Folder11', 'Folder12', 'Folder13', 'Folder14', 'Folder15', 'Folder16', 'Folder17', 'Folder18', 'Folder19', 'Folder20',
  ];

  //Dialog


  ngOnInit(): void {
    this.getFolders();
  }
  constructor(private _dialog: MatDialog) {
    this.filteredFolders = this.folderCtrl.valueChanges.pipe(
      startWith(''),
      map(folder => (folder ? this._filterFolders(folder) : this.folders.slice())),
    );
  }

  private _filterFolders(value: string): string[] {
    const filterValue = value.toLowerCase();
    return this.folders.filter(folder => folder.toLowerCase().includes(filterValue));
  }
  onFocus(): void {
    this.folderCtrl.setValue(this.folderCtrl.value);
  }

  getFolders(): void {

  }

  openAddFolder() {
    this.folderCtrl.setValue(null); // Réinitialiser la valeur
    this.folderCtrl.disable(); // Désactiver le champ de formulaire
    const dialogRef = this._dialog.open(AddFolderComponent, {});
    dialogRef.afterClosed().subscribe({
      next: (val) => {
        if (val) {
          this.getFolders();
          console.log('Folder added:', this.folderCtrl.value);
        }
      },
    });
  }




//// Dropzone
  onSelect(event: NgxDropzoneChangeEvent): void {
    this.files.push(...event.addedFiles.filter(file => file.type === 'application/pdf'));
  }

  onRemove(file: File): void {
    this.files = this.files.filter(f => f !== file);
  }

  uploadFiles() {

  }





}
