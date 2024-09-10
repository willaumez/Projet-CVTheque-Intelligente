import {Injectable} from '@angular/core';
import {HttpClient, HttpEvent} from "@angular/common/http";
import {Observable, throwError} from "rxjs";
import {catchError, map} from "rxjs/operators";
import {CVStatsDTO, FileDB, Folder} from "../../Models/FileDB";
import {environment} from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class FilesService {


  constructor(private http: HttpClient) {
  }

  //upload files

 /* uploadFiles(files: File[], folder: any): Observable<any> {
    const formData: FormData = new FormData();
    files.forEach(file => formData.append('files', file));
    formData.append('folder', JSON.stringify(folder));
    // @ts-ignore
    return this.http.post<HttpEvent<any>>(`${this.baseUrl}/file/upload`, formData, {
      reportProgress: true,
      observe: 'events'
    }).pipe(
      map((response) => {
        return response;
      })
    );
  }*/

  uploadFile(file: File, folder: any): Observable<any> {
    const formData: FormData = new FormData();
    formData.append('file', file);
    formData.append('folder', JSON.stringify(folder));

    return this.http.post(environment.backEndHost+"/file/upload", formData, {
      reportProgress: true,
      observe: 'events',
      responseType: 'text'
    });
  }


  //Get all files
  getAllFiles(folderId?: number): Observable<FileDB[]> {
    let url = environment.backEndHost + "/file/files";
    if (folderId !== undefined) {
      url += `?folderId=${folderId}`;
    }
    return this.http.get<FileDB[]>(url).pipe(
      catchError(error => {
        return throwError(() => new Error(error.message || 'Error fetching files'));
      })
    );
  }


  //Read file
  readFile(fileId: number | string) {
    return this.http.get(environment.backEndHost+`/file/read/${fileId}`, {observe: 'response', responseType: 'blob'});
  }

  //Delete all selected files
  deleteFiles(fileIds: number[]): Observable<any> {
    return this.http.delete(environment.backEndHost + "/file/delete/" + fileIds)
      .pipe(
        catchError(error => {
          return throwError(error);
        })
      );
  }


  transferFiles(data: any, selectedFolder: number) {
    const formData: FormData = new FormData();
    data.forEach((fileId: any) => {
      formData.append('fileIds', fileId.toString());
    });
    formData.append('folderId', selectedFolder.toString());
    return this.http.post(environment.backEndHost+ "/file/transfer", formData, {
      reportProgress: true,
      observe: 'events',
      responseType: 'text'
    });
  }

  renameFile(renameFileDB: FileDB) {
    if (!renameFileDB) {
      return;
    }
    return this.http.put(environment.backEndHost + "/file/update", renameFileDB, {
      reportProgress: true,
      observe: 'events',
      responseType: 'text'
    });
  }

  //Stats
  getCvCountPerMonth(): Observable<any> {
    return this.http.get<CVStatsDTO[]>(environment.backEndHost + "/file/count-per-month");
  }




}
