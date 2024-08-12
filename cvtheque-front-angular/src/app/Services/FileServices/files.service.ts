import {Injectable} from '@angular/core';
import {HttpClient, HttpEvent} from "@angular/common/http";
import {Observable, throwError} from "rxjs";
import {catchError, map} from "rxjs/operators";
import {FileDB, Folder} from "../../Models/FileDB";

@Injectable({
  providedIn: 'root'
})
export class FilesService {

  private baseUrl = 'http://localhost:8080';

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

    return this.http.post(`${this.baseUrl}/file/upload`, formData, {
      reportProgress: true,
      observe: 'events',
      responseType: 'text'
    });
  }


  //Get all files
  getAllFiles(): Observable<FileDB[]> {
    return this.http.get<FileDB[]>(`${this.baseUrl}/file/files`).pipe(
      catchError(error => {
        return throwError(error);
      })
    );
  }

  //Read file
  readFile(fileId: number | string) {
    return this.http.get(`${this.baseUrl}/file/read/${fileId}`, {observe: 'response', responseType: 'blob'});
  }

  //Delete all selected files
  deleteFiles(fileIds: number[]): Observable<any> {
    return this.http.delete(this.baseUrl + "/file/delete/" + fileIds)
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
    return this.http.post(`${this.baseUrl}/file/transfer`, formData, {
      reportProgress: true,
      observe: 'events',
      responseType: 'text'
    });
  }


}
