import {Component, ElementRef, inject, OnInit, ViewChild} from '@angular/core';
import {NgxDropzoneChangeEvent} from "ngx-dropzone";
import {FormControl, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {Observable, of, throwError} from 'rxjs';
import {catchError, map, startWith} from 'rxjs/operators';
import {MatDialog} from "@angular/material/dialog";
import {AddFolderComponent} from "../Dialog/add-folder/add-folder.component";
import {FilesService} from "../../Services/FileServices/files.service";
import {HttpErrorResponse, HttpEventType} from "@angular/common/http";

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

  constructor(private _dialog: MatDialog, private _fileService: FilesService) {
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


/////// Upload


  uploadFiles() {
    if (this.files.length === 0) {
      console.log('No files to upload.');
      return;
    }
    this.progress = 0;
    console.log('Uploading files:', this.files);

    this._fileService.uploadFiles(this.files).pipe(map((event: any) => {
        switch (event.type) {
          case HttpEventType.UploadProgress:
            // Calculer et mettre à jour la progression de l'upload
            console.log('Upload Progress:', event.loaded, event.total);
            this.progress = Math.round(((event.loaded-this.files.length) * 100 / event.total));
            break;
          case HttpEventType.Response:
            // Réinitialiser la progression après la réponse
            console.log('Files successfully uploaded:');
            this.files = [];
            this.progress = null;
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
