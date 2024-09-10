import {AfterViewInit, Component, inject, OnInit, ViewChild} from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {Folder} from "../../../Models/FileDB";
import {MatDialog} from "@angular/material/dialog";
import {AddProfileComponent} from "../../Dialog/add-profile/add-profile.component";
import {ProfileService} from "../../../Services/ProfileServices/profile.service";
import {Event} from "@angular/router";
import {CriteriaDB, Profile} from "../../../Models/Profile";
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
  selector: 'app-profile-criteria',
  templateUrl: './profile-criteria.component.html',
  styleUrl: './profile-criteria.component.scss',
})
/*export class ProfileCriteriaComponent implements AfterViewInit, OnInit {

  displayedColumns: string[] = ['name', 'actions'];
  dataSource: MatTableDataSource<Profile> = new MatTableDataSource<Profile>([]);
  errorMessage: string = '';
  isLoading: boolean = false;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private _dialog: MatDialog, private _profileService: ProfileService) {}

  ngOnInit(): void {
    this.getAllProfiles();
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  applyFilter(event: KeyboardEvent) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  addProfile() {
    const dialogRef = this._dialog.open(AddProfileComponent, {});
    dialogRef.afterClosed().subscribe({
      next: (val: Profile) => {
        if (val) {
          this.getAllProfiles();
        }
      },
    });
  }

  getAllProfiles() {
    this.isLoading = true;
    this._profileService.getAllProfiles().subscribe({
      next: (profiles: Profile[]) => {
        this.dataSource.data = profiles;
        this.isLoading = false;
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      },
      error: (error: Error) => {
        this.errorMessage = error.message;
        this.isLoading = false;
        console.error('Error getting profiles:', error);
      },
      complete: () => {
        this.isLoading = false;
      }
    });
  }
}*/
export class ProfileCriteriaComponent implements OnInit {
  dataSource: MatTableDataSource<Profile> = new MatTableDataSource<Profile>([]);
  errorMessage: string = '';
  isLoading: boolean = false;

  constructor(private _dialog: MatDialog, private _profileService: ProfileService) {
  }

  ngOnInit(): void {
    this.getAllProfiles();
    if (this.dataSource.data.length > 0) {
      this.selectedProfile = this.dataSource.data[0];
      console.log("premier element"+this.selectedProfile);
    }
  }

  applyFilter(event: KeyboardEvent) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  addProfile() {
    const dialogRef = this._dialog.open(AddProfileComponent, {});
    dialogRef.afterClosed().subscribe({
      next: (val: Profile) => {
        if (val) {
          this.getAllProfiles();
        }
      },
    });
  }

  getAllProfiles() {
    this.isLoading = true;
    this.errorMessage = '';
    this.criteriaErrorMessage = '';
    this._profileService.getAllProfiles().subscribe({
      next: (profiles: Profile[]) => {
        this.dataSource.data = profiles;
        this.isLoading = false;
      },
      error: (error: Error) => {
        this.errorMessage = error.message;
        this.isLoading = false;
      },
      complete: () => {
        this.setSelectedProfile();
        this.isLoading = false;
      }
    });
  }
  renameProfile(row: Profile) {
    const dialogRef = this._dialog.open(AddProfileComponent, {
      data: row
    });
    dialogRef.afterClosed().subscribe({
      next: (val: Profile) => {
        if (val) {
          this.getAllProfiles();
        }
      },
    });
  }

  deleteProfile(row: Profile) {
    this.errorMessage = '';
    this.criteriaErrorMessage = '';
    let conf: boolean = confirm("Are you sure to delete the item?");
    if (!conf) return;
    this._profileService.deleteProfile(row.id).subscribe({
      next: () => {
        this.selectedProfile = {} as Profile;
        this.getAllProfiles();
      },
      error: (error: Error) => {
        this.errorMessage = error.message;
      }
    });
  }


  //Criteria Functions
  getCriteria(id: number) {
    this.errorMessage = '';
    this.criteriaErrorMessage = '';
    this._profileService.getCriteriaByProfileId(id).subscribe({
      next: (criteria: CriteriaDB[]) => {
        this.selectedProfile.listCriteria = criteria;
      },
      error: (error: Error) => {
        this.criteriaErrorMessage = error.message;
      }
    });
  }
  selectedProfile!: Profile;
  criteriaErrorMessage: string = '';
  newCriteria: string = '';

  setSelectedProfile() {
    if (this.dataSource.data.length > 0) {
      this.selectedProfile = this.dataSource.data[0];
      this.getCriteria(this.selectedProfile.id);
    }
  }
  loadCriteria(row: Profile) {
    this.selectedProfile = row;
    this.getCriteria(row.id);
    console.log(row);
    //this.getCriteria(row.id);
  }
  addCriteriaClick() {
    this.errorMessage = '';
    this.criteriaErrorMessage = '';
    if(this.newCriteria && this.selectedProfile.id){
      this._profileService.addCriteria(this.selectedProfile.id, this.newCriteria).subscribe({
        next: (criteria: CriteriaDB) => {
          this.getCriteria(this.selectedProfile.id);
          this.newCriteria = '';
        },
        error: (error: Error) => {
          this.criteriaErrorMessage = error.message;
        }
      });
    }
  }
  deleteCriteria(id: number) {
    this.criteriaErrorMessage = '';
    this.errorMessage = '';
    let conf: boolean = confirm("Are you sure to delete the item?");
    if (!conf) return;
    this._profileService.deleteCriteria(id).subscribe({
      next: () => {
        this.getCriteria(this.selectedProfile.id);
      },
      error: (error: Error) => {
        this.criteriaErrorMessage = error.message;
      }
    });
  }





}

