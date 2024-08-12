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

    // Écoutez les changements de valeur du FormControl
    this.folderCtrl.valueChanges.subscribe(selectedFolder => {
      if (selectedFolder) {
        console.log('Dossier sélectionné:', selectedFolder);
      }
    });
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
        console.log('Folders fetched:', folders);
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
      next: (val) => {
        if (val) {
          this.setFolder(val);
          console.log('Folder added:', val);
        }
      },
    });
  }

  private setFolder(folderName: string): void {
    this.folderService.getAllFolders().subscribe({
      next: (folders: Folder[]) => {
        this.folders = folders;
        // Rechercher et sélectionner le dossier correspondant à `folderName`
        //console.log('Folders:', folders);
        let matchingFolder = folders.find(folder => folder.name === folderName);
        //console.log('Matching folder:', matchingFolder);
        if (matchingFolder) {
          this.folderCtrl.setValue(matchingFolder);
        } else {
          console.error('Folder not found:', folderName);
        }
      },
      error: (error) => {
        console.error('Error fetching folders:', error);
      }
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


  /*uploadFiles() {
    if (this.files.length === 0  || !this.folderCtrl.value) {
      console.log('No files or folder data available.');
      return;
    }
    this.progress = 0;

    this._fileService.uploadFiles(this.files, this.folderCtrl.value).pipe(map((event: any) => {
      switch (event.type) {
        case HttpEventType.UploadProgress:
          // Calculer et mettre à jour la progression de l'upload
          console.log('Upload Progress:', event.loaded, event.total);
          this.progress = Math.round(((event.loaded - this.files.length) * 100 / event.total));
          break;
        case HttpEventType.Response:
          // Réinitialiser la progression après la réponse
          console.log('Files successfully uploaded:');
          this.files = [];
          this.progress = null;
          this.folderCtrl.setValue(null);
          break;
      }
    }), catchError((error: HttpErrorResponse) => {
      this.progress = null;
      return of('Upload failed: ' + error.message);
    })).subscribe((event: any) => {
      if (typeof event === 'string') {
        console.error(event);
      }
    });
  }*/

  uploadFiles() {
    if (this.files.length === 0 || !this.folderCtrl.value) {
      console.log('No files or folder data available.');
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
                console.log('Upload Progress:', this.progress, '%');
              }
              break;
            case HttpEventType.Response:
              // Incrémenter le nombre de fichiers uploadés et mettre à jour la progression globale
              filesUploaded++;
              this.progress = Math.round((filesUploaded * 100) / nbFiles);
              console.log('File successfully uploaded:', event.body);

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
          console.error('Upload failed:', error.message);
          return of('Upload failed: ' + error.message);
        })
      ).subscribe();
    });
  }


  // Méthode pour gérer l'upload des fichiers
  /*uploadFiles() {
    if (this.files.length === 0) {
      console.log('No files to upload.');
      return;
    }

    this.progress = 1;
    console.log('Uploading files:', this.files);

    this._fileService.uploadFiles(this.files).subscribe({
      next: (event: any) => {
        if (event.type === HttpEventType.UploadProgress) {
          // Calculer et mettre à jour la progression de l'upload
            this.progress = Math.round((100 / event.total) * event.loaded);
        } else if (event.type === HttpEventType.Response) {
          // Réinitialiser la progression après la réponse
          this.progress = null;
          console.log('Files successfully uploaded:', event.body);
          this.files = [];
        }
      },
      error: (error: any) => {
        this.progress = null;
        console.error('Upload failed:', error);
      }
    });
  }*/


  /*
    uploadFiles() {
      console.log('Uploading files:', this.files);
      this._fileService.uploadFiles(this.files).subscribe({
        next: (event: any) => {
          console.log(event);
          this.files = [];
        },
        error: (error) => {
          console.error(error);
        }
      });
    }
  */


}
