import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {Folder} from "../../Models/FileDB";
import {Observable, of, throwError} from "rxjs";
import {environment} from "../../../environments/environment";
import {catchError, map} from "rxjs/operators";
import {CriteriaDB, Profile} from "../../Models/Profile";

@Injectable({
  providedIn: 'root'
})
export class ProfileService {

  constructor(private http: HttpClient) {
  }

  //Save folder
  saveProfile(profile: Profile): Observable<Profile> {
    return this.http.post<Profile>(environment.backEndHost+"/profile/save", profile).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 409) {
          return throwError(() => new Error(error.error));
        } else {
          return throwError(() => new Error('An unexpected error occurred.'));
        }
      })
    );
  }

  //Get all folders
  getAllProfiles(): Observable<Profile[]> {
    return this.http.get<Profile[]>(environment.backEndHost+"/profile/profiles").pipe(
      map((response) => {
        return response;
      }),
      catchError(error => {
        return throwError(error);
      })
    );
  }

  //Get Criteria by Profile ID
  getCriteriaByProfileId(profileId: number): Observable<CriteriaDB[]> {
    return this.http.get<CriteriaDB[]>(environment.backEndHost+"/profile/criteria/"+profileId).pipe(
      map((response) => {
        return response;
      }),
      catchError(error => {
        return throwError(error);
      })
    );
  }
  //Add Criteria
  /*addCriteria(id: number, newCriteria: string) {
    return this.http.post<CriteriaDB>(environment.backEndHost+"/profile/criteria/add", {profileId: id, description: newCriteria}).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 409) {
          return throwError(() => new Error(error.error));
        } else {
          return throwError(() => new Error('An unexpected error occurred.'));
        }
      })
    );
  }*/
  addCriteria(id: number, newCriteria: string) {
    return this.http.post<CriteriaDB>(environment.backEndHost + "/profile/criteria/add", {id: id, description: newCriteria}).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 409) {
          return throwError(() => new Error("This criteria already exists for this profile."+error.error));
        } else {
          return throwError(() => new Error("An unexpected error occurred."));
        }
      })
    );
  }

  //Delete Criteria
  deleteCriteria(id: number) {
    return this.http.delete(environment.backEndHost + "/profile/criteria/delete/" + id).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 409) {
          return throwError(() => new Error(error.error));
        } else if (error.status === 404) {
          return throwError(() => new Error('Criteria not found'));
        } else if (error.status >= 400 && error.status < 600) {
          return throwError(() => new Error('An unexpected error occurred: ' + error.message));
        } else {
          return of(null);
        }
      })
    );
  }



}
