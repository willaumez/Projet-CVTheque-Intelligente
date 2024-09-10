import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Folder} from "../../../Models/FileDB";
import {ProfileService} from "../../../Services/ProfileServices/profile.service";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {Profile} from "../../../Models/Profile";

@Component({
  selector: 'app-add-profile',
  templateUrl: './add-profile.component.html',
  styleUrl: './add-profile.component.scss'
})
export class AddProfileComponent implements OnInit {
  addProfileForm: FormGroup;
  errorMessage!: string;

  constructor( private fb: FormBuilder, private _profileService: ProfileService,
               @Inject(MAT_DIALOG_DATA) public data: any, private _dialogRef: MatDialogRef<AddProfileComponent>) {
    this.addProfileForm = this.fb.group({
      id: null,
      name: ['', Validators.required],
      description: '',
      createdAt: null,
      listCriteria: null
    });
  }
  ngOnInit(): void {
    if (this.data && this.data?.id) {
      this.addProfileForm = this.fb.group({
        id: this.data.id,
        name: this.data.name,
        description: this.data.description,
        createdAt: this.data.createdAt,
        listCriteria: null
      });
      console.log(JSON.stringify(this.addProfileForm.value));
    }
  }

  addProfileSubmit() {
    if (this.addProfileForm.valid) {
      const formData = this.addProfileForm.value;
      this._profileService.saveProfile(formData).subscribe({
        next: (val: Profile) => {
          this._dialogRef.close(val);
        },
        error: (err) => {
          this.errorMessage = err.message;
        }
      });
    } else {
      console.log('Invalid form');
    }
  }

  // Méthode pour vérifier si le formulaire est valide

  isFormValid(): boolean {
    return this.addProfileForm.valid;
  }




}
