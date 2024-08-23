import { Injectable } from '@angular/core';
import {HttpClient, HttpEvent} from "@angular/common/http";
import {Observable} from "rxjs";
import {FileDB} from "../../Models/FileDB";

@Injectable({
  providedIn: 'root'
})
export class AiChatService {

  private baseUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) { }

  // Méthode pour poser une question au service de chat
  askQuestion(question: string): Observable<string> {
    return this.http.get<string>(`${this.baseUrl}/ai/chat`, {
      params: { question },
      responseType: 'text' as 'json'
    });
  }


  selectWithCriteria(selectedCriteria: string[], jobDescription: string): Observable<HttpEvent<Observable<FileDB[]>>> {
    const formData: FormData = new FormData();
    selectedCriteria.forEach((criteria: any) => {
      formData.append('selectedCriteria', criteria);
    });
    formData.append('jobDescription', jobDescription);
    return this.http.post<Observable<FileDB[]>>(`${this.baseUrl}/ai/criteria`, formData, {
      reportProgress: true,
      observe: 'events',
      responseType: 'text' as 'json'
    });
  }


}
