import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

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


}
