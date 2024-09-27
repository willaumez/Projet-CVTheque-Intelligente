import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {CdkTextareaAutosize} from "@angular/cdk/text-field";
import {FoldersService} from "../../../Services/FolderServices/folders.service";
import {Folder} from "../../../Models/FileDB";
import {FilesService} from "../../../Services/FileServices/files.service";

@Component({
  selector: 'app-add-folder',
  templateUrl: './add-folder.component.html',
  styleUrl: './add-folder.component.scss'
})
export class AddFolderComponent implements OnInit {
  addFolderForm: FormGroup;
  folders: Folder[] = [];
  isTransfer: boolean = false;
  selectedFolder: number | null = null;

  inOperation: boolean = false;

  @ViewChild('autosize') autosize: CdkTextareaAutosize | undefined;
  error!: string;

  constructor(private fb: FormBuilder, private _dialogRef: MatDialogRef<AddFolderComponent>, private _folderService: FoldersService,
              @Inject(MAT_DIALOG_DATA) public data: any, private folderService: FoldersService, private _fileService: FilesService) {
    this.addFolderForm = this.fb.group({
      id: null,
      name: ['', Validators.required],
      description: [''],
      createdAt: null
    });
  }

  ngOnInit(): void {
    if (this.data && this.data?.id) {
      this.addFolderForm = this.fb.group({
        id: this.data.id,
        name: this.data.name,
        description: this.data.description,
        createdAt: this.data.createdAt,
      });
    } else if (this.data?.transferFromFolder!) {
      this.isTransfer = true;
      this.getFolders();
      this.selectedFolder = this.data.folderId;
    } else if (this.data) {
      this.getFolders();
      this.isTransfer = true;
    }
  }

// Méthode appelée lors de la soumission du formulaire;
  addFolderSubmit(): void {
    let newFolder: Folder = undefined!;
    if (this.addFolderForm.valid) {
      const formData = this.addFolderForm.value;
      this._folderService.saveFolder(formData).subscribe({
        next: (folder: Folder) => {
          newFolder = folder;
        },
        error: (error) => {
          this.error = 'Error saving folder: ' + error.message;
        },
        complete: () => {
          this._dialogRef.close(newFolder);
        }
      });
    } else {
      this.error = 'Please fill in the required fields';
    }
  }

// Méthode pour vérifier si le formulaire est valide
  isFormValid(): boolean {
    return this.addFolderForm.valid;
  }


  getFolders(): void {
    this.inOperation = true;
    this.folderService.getAllFolders().subscribe({
      next: (folders: Folder[]) => {
        this.folders = folders;
        this.inOperation = false;
      },
      error: (error) => {
        this.inOperation = true;
        this.error = 'Error fetching folders: ' + error.message;
      }
    });
  }

  transferSubmit() {
    this.inOperation = true;
    if (this.selectedFolder && this.data && this.data.length > 0) {
      this._fileService.transferFiles(this.data, this.selectedFolder).subscribe({
        error: (error) => {
          this.error = 'Error transferring files: ' + error.message;
        },
        complete: () => {
          this.inOperation = false;
          this.isTransfer = false;
          //console.log('this._dialogRef.close(true);');
          this._dialogRef.close(true);
        }
      });
    } else {
      this.inOperation = false;
      this.isTransfer = false;
      //console.warn("No folder selected or no files to transfer");
    }
  }


  transferFromIdSubmit() {
    this.inOperation = true;
    if (this.selectedFolder && this.data && this.data.transferFromFolder) {
      this.folderService.transferFilesFromId(this.data.folderId, this.selectedFolder).subscribe({
        error: (error) => {
          this.inOperation = false;
          this.isTransfer = false;
          //console.error("Error transferring files", error);
        },
        complete: () => {
          this.inOperation = false;
          this.isTransfer = false;
          this._dialogRef.close(true);
        }

      });
    } else {
      this.inOperation = false;
      this.isTransfer = false;
    }
  }
}
