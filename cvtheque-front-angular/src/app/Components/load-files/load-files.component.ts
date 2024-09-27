import {Component, OnInit} from '@angular/core';
import {NgxDropzoneChangeEvent} from "ngx-dropzone";
import {FormControl} from '@angular/forms';
import {Observable, of} from 'rxjs';
import {catchError, map, startWith} from 'rxjs/operators';
import {MatDialog} from "@angular/material/dialog";
import {AddFolderComponent} from "../Dialog/add-folder/add-folder.component";
import {FilesService} from "../../Services/FileServices/files.service";
import {HttpErrorResponse, HttpEventType} from "@angular/common/http";
import {FoldersService} from "../../Services/FolderServices/folders.service";
import {Folder} from "../../Models/FileDB";

@Component({
  selector: 'app-load-files',
  templateUrl: './load-files.component.html',
  styleUrl: './load-files.component.scss'
})
export class LoadFilesComponent implements OnInit {
  //progress
  progress: number | null = null;
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
      },
      error: (error) => {
      }
    });
  }

  displayFolder(folder: Folder): string {
    return folder ? folder.name : '';
  }


  // Ouvrir la boîte de dialogue pour ajouter un dossier
  openAddFolder() {
    this.folderCtrl.setValue(null); // Réinitialiser la valeur
    const dialogRef = this._dialog.open(AddFolderComponent, {});
    dialogRef.afterClosed().subscribe(
       (val: Folder) => {
        if (val) {
          this.folders.push(val);
          this.folderCtrl.setValue(val);
          this.onFocus();
          this.displayFolder(val);
        }
      },
    );
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
              if (event.total) {
                const fileProgress = Math.round((100 / event.total) * event.loaded);
                this.progress = Math.round((filesUploaded + fileProgress / 100) * 100 / nbFiles);
              }
              break;
            case HttpEventType.Response:
              filesUploaded++;
              this.progress = Math.round((filesUploaded * 100) / nbFiles);
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
          return of('Upload failed: ' + error.message);
        })
      ).subscribe();
    });
  }


}
