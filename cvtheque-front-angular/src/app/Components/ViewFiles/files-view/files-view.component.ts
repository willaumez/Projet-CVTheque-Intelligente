import {Component} from '@angular/core';
import {
  CdkDragDrop,
  moveItemInArray,
  transferArrayItem,
} from '@angular/cdk/drag-drop';
import {AiChatService} from "../../../Services/AiServices/ai-chat.service";
import {FileDB} from "../../../Models/FileDB";
import {BehaviorSubject, Observable, Subject} from "rxjs";
import {HttpEventType} from "@angular/common/http";

@Component({
  selector: 'app-files-view',
  templateUrl: './files-view.component.html',
  styleUrl: './files-view.component.scss'
})

export class FilesViewComponent {
  inputValue: string = '';
  isListView: boolean = true;
  isGridView: boolean = false;

  loading = false;
  error: string = '';

  // Utilisation de BehaviorSubject avec un tableau vide par défaut
  data$: BehaviorSubject<FileDB[]> = new BehaviorSubject<FileDB[]>([]);

  constructor(private _aiService: AiChatService) {}

  toggleListView() {
    this.isListView = true;
    this.isGridView = false;
  }

  toggleGridView() {
    this.isGridView = true;
    this.isListView = false;
  }

  criteria: string[] = [
    "5 ans d'expérience en developpement web",
    "posseder un diplome en informatique",
    "Maitrise des technologies Java, Angular et Spring",
    "Compétences interpersonnelles (communication, travail en équipe, leadership)",
    "Résultats et réalisations tangibles mentionnés",
    "Adéquation avec la culture d'entreprise",
    "Clarté et organisation du CV",
    "Motivation et qualité de la lettre de motivation"
  ];

  selectedCriteria: string[] = [];
  jobDescription: string = '';
  files: FileDB[] = [];

  drop(event: CdkDragDrop<string[]>) {
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex,
      );
    }
  }

  click() {
    this.loading = true;
    this._aiService.selectWithCriteria(this.selectedCriteria, this.jobDescription).subscribe(
      (event: any) => {
        if (event.type === HttpEventType.Response) {
          const response: FileDB[] = JSON.parse(event.body);
          this.data$.next(response); // Émettre la nouvelle valeur
          this.loading = false;
        }
      },
      (error) => {
        this.error = 'Error fetching files: ' + error.message;
        this.loading = false;
      }
    );
  }
  cancelData() {
    this.selectedCriteria = [];
    this.jobDescription = '';
    this.data$.next([]);
  }


  //protected readonly event = event;

  onIconClick() {
    this.selectedCriteria.push(this.inputValue);
    this.inputValue = '';
  }


}

