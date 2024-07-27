import {Component, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {MatDialogRef} from "@angular/material/dialog";
import {CdkTextareaAutosize} from "@angular/cdk/text-field";

@Component({
  selector: 'app-add-folder',
  templateUrl: './add-folder.component.html',
  styleUrl: './add-folder.component.scss'
})
export class AddFolderComponent implements OnInit{
  addFolderForm: FormGroup;

  @ViewChild('autosize') autosize: CdkTextareaAutosize | undefined;

  constructor(private fb: FormBuilder, private _dialogRef: MatDialogRef<AddFolderComponent>) {
    // Initialisation du formulaire
    this.addFolderForm = this.fb.group({
      folderName: ['', Validators.required],
      folderDescription: ['']
    });
  }

  ngOnInit(): void {
    // Actions à effectuer à l'initialisation du composant
  }

  // Méthode appelée lors de la soumission du formulaire
  addFolderSubmit(): void {
    if (this.addFolderForm.valid) {
      const formData = this.addFolderForm.value;
      console.log('Formulaire soumis avec succès:', formData);
      this._dialogRef.close(true);
      // Ajouter votre logique de soumission ici (ex: appeler un service pour sauvegarder les données)
    } else {
      console.log('Formulaire invalide');
    }
  }

  // Méthode pour vérifier si le formulaire est valide
  isFormValid(): boolean {
    return this.addFolderForm.valid;
  }
}
