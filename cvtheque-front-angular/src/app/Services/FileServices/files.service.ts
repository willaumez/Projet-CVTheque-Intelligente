import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable, throwError} from "rxjs";
import {catchError, map} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class FilesService {

  private baseUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) { }

  //upload files

  uploadFiles(files: File[]): Observable<any> {
    const formData: FormData = new FormData();
    files.forEach(file => formData.append('files', file));

    // @ts-ignore
    return this.http.post<HttpEvent<any>>(`${this.baseUrl}/file/upload`, formData, {
      reportProgress: true,
      observe: 'events'
    }).pipe(
      map((response) => {
        return response;
      })
    );
  }

  /*uploadFiles(files: File[]): Observable<any> {
    const formData: FormData = new FormData();
    files.forEach(file => formData.append('files', file));

    // @ts-ignore
    return this.http.post<HttpEvent<any>>(`${this.baseUrl}/file/upload`, formData)
      .pipe(
        catchError(error => {
          return throwError("Erreur lors de l'envoi des fichiers");
        })
      );
  }*/

 /* uploadFiles(files: File[]): Observable<any> {
    const formData: FormData = new FormData();
    files.forEach(file => formData.append('files', file));

    // @ts-ignore
    return this.http.post<HttpEvent<any>>(`${this.baseUrl}/file/upload`, formData)
      .pipe(
        catchError(error => {
          return throwError("Erreur lors de l'envoi des fichiers");
        })
      );
  }*/


  /*

    uploadMultipleFiles(files: File[]): Observable<HttpEvent<any>> {
      const formData: FormData = new FormData();
      files.forEach(file => formData.append('files', file));

      // @ts-ignore
      return this.http.post<HttpEvent<any>>(`${this.baseUrl}/upload`, formData, {
        headers: new HttpHeaders(),
        reportProgress: true,
        observe: 'events'
      }).pipe(
        map(event => {
          switch (event.type) {
            case HttpEventType.UploadProgress:
              if (event.total) {
                const percentDone = Math.round(100 * event.loaded / event.total);
                return { status: 'progress', percentDone };
              }
              break;
            case HttpEventType.Response:
              return { status: 'complete', body: event.body };
          }
          return { status: 'unknown' };
        }),
        catchError(error => of({ status: 'error', error }))
      );
    }

    fetchFileNames(): Observable<string[]> {
      return this.http.get<string[]>(`${this.baseUrl}/files`);
    }
  */

}
