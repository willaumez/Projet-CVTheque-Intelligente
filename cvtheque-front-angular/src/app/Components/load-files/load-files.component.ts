import {Component, ElementRef, inject, OnInit, ViewChild} from '@angular/core';
import {NgxDropzoneChangeEvent} from "ngx-dropzone";
import {FormControl, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {Observable, of, throwError} from 'rxjs';
import {catchError, map, startWith} from 'rxjs/operators';
import {MatDialog} from "@angular/material/dialog";
import {AddFolderComponent} from "../Dialog/add-folder/add-folder.component";
import {FilesService} from "../../Services/FileServices/files.service";
import {HttpErrorResponse, HttpEventType} from "@angular/common/http";
import {FoldersService} from "../../Services/FolderServices/folders.service";
import {error} from "@angular/compiler-cli/src/transformers/util";
import {Folder} from "../../Models/FileDB";

//import {FileService} from "../../Services/file-service.service";


@Component({
  selector: 'app-load-files',
  templateUrl: './load-files.component.html',
  styleUrl: './load-files.component.scss'
})
export class LoadFilesComponent implements OnInit {
  //progress
  progress: number | null = null;


  //
  files: File[] = [];

  folderCtrl = new FormControl<Folder | null>(null);
  filteredFolders: Observable<Folder[]>;
  folders: Folder[] = [];

  ngOnInit(): void {
    this.getFolders();
  }

  constructor(private _dialog: MatDialog, private _fileService: FilesService, private folderService: FoldersService) {
    this.filteredFolders = this.folderCtrl.valueChanges.pipe(
      startWith(''),
      map(folder => (typeof folder === 'string' ? folder : folder?.name || '')),
      map(name => (name ? this._filterFolders(name) : this.folders.slice())),
    );
  }

  private _filterFolders(value: string): Folder[] {
    const filterValue = value.toLowerCase();
    return this.folders.filter(folder => folder.name.toLowerCase().includes(filterValue));
  }

  onFocus(): void {
    this.folderCtrl.setValue(this.folderCtrl.value);
  }

  getFolders(): void {
    this.folderService.getAllFolders().subscribe({
      next: (folders: Folder[]) => {
        this.folders = folders;
        //console.log('Folders fetched:', folders);
      },
      error: (error) => {
        console.error('Error fetching folders:', error);
      }
    });
  }

  displayFolder(folder: Folder): string {
    return folder ? folder.name : '';
  }


  // Ouvrir la boîte de dialogue pour ajouter un dossier
  openAddFolder() {
    this.folderCtrl.setValue(null); // Réinitialiser la valeur
    //this.folderCtrl.disable(); // Désactiver le champ de formulaire
    const dialogRef = this._dialog.open(AddFolderComponent, {});
    dialogRef.afterClosed().subscribe({
      next: (val: Folder) => {
        if (val) {
          this.folders.push(val);
          this.folderCtrl.setValue(val);
          this.onFocus();
          this.displayFolder(val);
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


/////// Upload
  uploadFiles() {
    if (this.files.length === 0 || !this.folderCtrl.value) {
      //console.log('No files or folder data available.');
      return;
    }

    const nbFiles = this.files.length;
    let filesUploaded = 0;
    this.progress = 0;

    // Parcourir les fichiers et les envoyer un par un
    this.files.forEach(file => {
      this._fileService.uploadFile(file, this.folderCtrl.value).pipe(
        map((event: any) => {
          switch (event.type) {
            case HttpEventType.UploadProgress:
              // Calculer et mettre à jour la progression de l'upload
              if (event.total) {
                const fileProgress = Math.round((100 / event.total) * event.loaded);
                this.progress = Math.round((filesUploaded + fileProgress / 100) * 100 / nbFiles);
                //console.log('Upload Progress:', this.progress, '%');
              }
              break;
            case HttpEventType.Response:
              // Incrémenter le nombre de fichiers uploadés et mettre à jour la progression globale
              filesUploaded++;
              this.progress = Math.round((filesUploaded * 100) / nbFiles);
              //console.log('File successfully uploaded:', event.body);

              // Si tous les fichiers sont uploadés, réinitialiser les valeurs
              if (filesUploaded === nbFiles) {
                this.progress = null;
                this.files = [];
                this.folderCtrl.setValue(null);
              }
              break;
          }
        }),
        catchError((error: HttpErrorResponse) => {
          this.progress = null;
          //console.error('Upload failed:', error.message);
          return of('Upload failed: ' + error.message);
        })
      ).subscribe();
    });
  }


}
