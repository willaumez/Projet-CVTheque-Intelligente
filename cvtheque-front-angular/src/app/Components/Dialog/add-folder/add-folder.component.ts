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
    } else if (this.data?.transferFromFolder!){
      this.isTransfer = true;
      this.getFolders();
      this.selectedFolder = this.data.folderId;
    }else if (this.data) {
      this.getFolders();
      this.isTransfer = true;
    }
  }

// Méthode appelée lors de la soumission du formulaire
  addFolderSubmit(): void {
    if (this.addFolderForm.valid) {
      const formData = this.addFolderForm.value;
      this._folderService.saveFolder(formData).subscribe(
        (response: Folder) => {
          console.log('----- Folder added:', response);
          this._dialogRef.close(response);
        },
        (error) => {
          console.error('Erreur lors de l\'ajout du dossier:', error);
        }
      );

      this._dialogRef.close();
      // Ajouter votre logique de soumission ici (ex: appeler un service pour sauvegarder les données)
    } else {
      console.log('Formulaire invalide');
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
        console.error('Error fetching folders:', error);
      }
    });
  }

  transferSubmit() {
    this.inOperation = true;
    if (this.selectedFolder && this.data && this.data.length > 0) {
      this._fileService.transferFiles(this.data, this.selectedFolder).subscribe(
        (response) => {
          this.inOperation = false;
          this.isTransfer = false;
          this._dialogRef.close(true);
        },
        (error) => {
          this.inOperation = false;
          this.isTransfer = false;
          //console.error("Error transferring files", error);
        }
      );
    } else {
      this.inOperation = false;
      this.isTransfer = false;
      //console.warn("No folder selected or no files to transfer");
    }
  }
  transferFromIdSubmit() {
    this.inOperation = true;
    if (this.selectedFolder && this.data && this.data.transferFromFolder) {
      this.folderService.transferFilesFromId(this.data.folderId, this.selectedFolder).subscribe(
        (response) => {
          this.inOperation = false;
          this.isTransfer = false;
          this._dialogRef.close(true);
        },
        (error) => {
          this.inOperation = false;
          this.isTransfer = false;
          //console.error("Error transferring files", error);
        }
      );
    } else {
      this.inOperation = false;
      this.isTransfer = false;
      //console.warn("No folder selected or no files to transfer");
    }
  }
}
