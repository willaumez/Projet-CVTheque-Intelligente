import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Folder} from "../../Models/FileDB";
import {map} from "rxjs/operators";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class FoldersService {
  private baseUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {
  }

  //Save folder
  saveFolder(folder: Folder): Observable<Folder> {
    return this.http.post<Folder>(`${this.baseUrl}/folder/save`, folder).pipe(
      map((response) => {
        return response;
      })
    );
  }

  //Get all folders
  getAllFolders(): Observable<Folder[]> {
    return this.http.get<Folder[]>(`${this.baseUrl}/folder/folders`).pipe(
      map((response) => {
        return response;
      })
    );
  }


}
