import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Folder} from "../../Models/FileDB";
import {catchError, map} from "rxjs/operators";
import {Observable, of, throwError} from "rxjs";
import {environment} from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class FoldersService {

  constructor(private http: HttpClient) {
  }

  //Save folder
  saveFolder(folder: Folder): Observable<Folder> {
    return this.http.post<Folder>(environment.backEndHost + "/folder/save", folder).pipe(
      map((response) => {
        return response;
      })
    );
  }

  //Get all folders
  getAllFolders(kw: string = ""): Observable<Folder[]> {
    const params = new HttpParams()
      .set('kw', kw);
    return this.http.get<Folder[]>(environment.backEndHost + "/folder/folders", {params}).pipe(
      map((response) => {
        return response;
      }),
      catchError(error => {
        return throwError(error);
      })
    );
  }
  //Transfer folder's Files
  transferFilesFromId(folderId: number, selectedFolder: number) {
    const formData: FormData = new FormData();
    formData.append('fromId', folderId.toString());
    formData.append('toId', selectedFolder.toString());
    return this.http.post(environment.backEndHost+ "/folder/transfer", formData, {
      reportProgress: true,
      observe: 'events',
      responseType: 'text'
    });
  }

  delete(folderId: number) {
    return this.http.delete(environment.backEndHost+"/folder/delete/"+folderId, {
      responseType: 'text'
    });
  }

  deleteAll(selection: number[]) {
    const ids = selection.join(',');
    return this.http.delete(environment.backEndHost + "/folder/deletes?ids=" + ids, {
      responseType: 'text'
    });
  }

  getFolder(folderId: number) {
    return this.http.get<Folder>(`${environment.backEndHost}/folder/folders/${folderId}`).pipe(
      catchError(error => {
        console.error('Error fetching folder:', error);
        return of(null);
      })
    );
  }


}
