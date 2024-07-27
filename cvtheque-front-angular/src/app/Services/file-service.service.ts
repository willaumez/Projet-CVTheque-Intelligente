import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpEventType, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class FileService {
  private baseUrl = 'https://your-api-endpoint'; // Remplacez par l'URL de votre API

  constructor(private http: HttpClient) { }

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
}
