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
  selectedFolder: number | null = null;

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
    if (this.data) {
      this.getFolders()
    }
  }

// Méthode appelée lors de la soumission du formulaire
  addFolderSubmit(): void {
    if (this.addFolderForm.valid) {
      const formData = this.addFolderForm.value;
      this._folderService.saveFolder(formData).subscribe(
        (response) => {
          console.log('Folder ajouté avec succès:', response);
        },
        (error) => {
          console.error('Erreur lors de l\'ajout du dossier:', error);
        }
      );

      this._dialogRef.close(this.addFolderForm.value.name);
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
    this.folderService.getAllFolders().subscribe({
      next: (folders: Folder[]) => {
        this.folders = folders;
      },
      error: (error) => {
        console.error('Error fetching folders:', error);
      }
    });
  }

  transferSubmit() {
    if (this.selectedFolder && this.data && this.data.length > 0) {
      this._fileService.transferFiles(this.data, this.selectedFolder).subscribe(
        (response) => {
          this._dialogRef.close(true);
        },
        (error) => {
          console.error("Error transferring files", error);
        }
      );
    } else {
      console.warn("No folder selected or no files to transfer");
    }
  }


}
