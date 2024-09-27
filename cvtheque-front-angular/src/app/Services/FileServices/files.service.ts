import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpParams} from "@angular/common/http";
import {Observable, of, throwError} from "rxjs";
import {catchError} from "rxjs/operators";
import {CVStatsDTO, FileDB} from "../../Models/FileDB";
import {environment} from "../../../environments/environment";
import {EvaluationDto} from "../../Models/EvaluationDto";

@Injectable({
  providedIn: 'root'
})
export class FilesService {


  constructor(private http: HttpClient) {
  }

  //upload files
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
  getAllHomeDta(): Observable<any> {
    return this.http.get<CVStatsDTO[]>(environment.backEndHost + "/file/getStats");
  }

  //Get Evaluation
  getEvaluationInfos(file: number) {
    return this.http.get<EvaluationDto>(environment.backEndHost + `/file/evaluation/${file}`);
  }
  //Delete Evaluation
  deleteEvaluation(evaluationId: number) {
    return this.http.delete(environment.backEndHost + `/file/evaluation/${evaluationId}`).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 409) {
          return throwError(() => new Error(error.error));
        } else if (error.status === 404) {
          return throwError(() => new Error('Evaluation not found'));
        } else if (error.status >= 400 && error.status < 600) {
          return throwError(() => new Error('An unexpected error occurred: ' + error.message));
        } else {
          return of(null);
        }
      })
    );
  }
  //Delete all evaluations

  deleteAllEvaluation(selection: number[]) {
    let params = new HttpParams();
    selection.forEach(id => {
      params = params.append('selection', id.toString());
    });

    return this.http.delete(environment.backEndHost+"/file/evaluation/delete", { params })
      .pipe(
        catchError((error: HttpErrorResponse) => {
          if (error.status === 409) {
            return throwError(() => new Error(error.error));
          } else if (error.status === 404) {
            return throwError(() => new Error('Evaluation not found'));
          } else if (error.status >= 400 && error.status < 600) {
            return throwError(() => new Error('An unexpected error occurred: ' + error.message));
          } else {
            return of(null);
          }
        })
      );
  }


}
