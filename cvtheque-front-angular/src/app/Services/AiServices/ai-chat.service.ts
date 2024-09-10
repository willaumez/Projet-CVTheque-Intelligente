import { Injectable } from '@angular/core';
import {HttpClient, HttpEvent} from "@angular/common/http";
import {Observable} from "rxjs";
import {FileDB} from "../../Models/FileDB";
import {environment} from "../../../environments/environment";

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


  selectWithCriteria(criteria: { selectedCriteria: string[], jobDescription: string, folderId?: number }): Observable<HttpEvent<Observable<FileDB[]>>> {
    const formData: FormData = new FormData();

    criteria.selectedCriteria.forEach((criteriaItem: string) => {
      formData.append('selectedCriteria', criteriaItem);
    });

    formData.append('jobDescription', criteria.jobDescription);
    if (criteria.folderId !== undefined) {
      formData.append('folderId', criteria.folderId.toString());
    }
    return this.http.post<Observable<FileDB[]>>(environment.backEndHost+ "/ai/criteria", formData, {
      reportProgress: true,
      observe: 'events',
      responseType: 'text' as 'json'
    });
  }

  selectWithKeywords(keywords: { keywords: string[]; folderId?: number }) {
    const formData: FormData = new FormData();

    keywords.keywords.forEach((criteriaItem: string) => {
      formData.append('keywords', criteriaItem);
    });
    if (keywords.folderId !== undefined) {
      formData.append('folderId', keywords.folderId.toString());
    }
    return this.http.post<Observable<FileDB[]>>(environment.backEndHost+ "/ai/keywords", formData, {
      reportProgress: true,
      observe: 'events',
      responseType: 'text' as 'json'
    });

  }


  selectByScoring(scoring: { jobDescription: string; folderId: number | undefined }) {
    const formData: FormData = new FormData();
    formData.append('jobDescription', scoring.jobDescription);
    if (scoring.folderId !== undefined) {
      formData.append('folderId', scoring.folderId.toString());
    }
    console.log('Scoring: ', scoring);
    return this.http.post<Observable<FileDB[]>>(environment.backEndHost+"/ai/scoring", formData, {
      reportProgress: true,
      observe: 'events',
      responseType: 'text' as 'json'
    });
  }


}
