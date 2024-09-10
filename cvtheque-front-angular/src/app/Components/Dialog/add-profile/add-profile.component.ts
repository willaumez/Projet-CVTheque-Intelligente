import { Component } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Folder} from "../../../Models/FileDB";
import {ProfileService} from "../../../Services/ProfileServices/profile.service";
import {MatDialogRef} from "@angular/material/dialog";
import {Profile} from "../../../Models/Profile";

@Component({
  selector: 'app-add-profile',
  templateUrl: './add-profile.component.html',
  styleUrl: './add-profile.component.scss'
})
export class AddProfileComponent {
  addProfileForm: FormGroup;
  errorMessage!: string;

  constructor( private fb: FormBuilder, private _profileService: ProfileService, private _dialogRef: MatDialogRef<AddProfileComponent>) {
    this.addProfileForm = this.fb.group({
      id: null,
      name: ['', Validators.required],
      description: '',
      createdAt: null,
      listCriteria: null
    });
  }


  addProfileSubmit() {
    if (this.addProfileForm.valid) {
      const formData = this.addProfileForm.value;
      this._profileService.saveProfile(formData).subscribe(
        (response: Profile) => {
          this._dialogRef.close(response);
        },
        (error: Error) => {
          this.errorMessage = error.message;
          console.error('Error adding the profile:', error);
        }
      );
    } else {
      console.log('Invalid form');
    }
  }

  // Méthode pour vérifier si le formulaire est valide

  isFormValid(): boolean {
    return this.addProfileForm.valid;
  }




}
