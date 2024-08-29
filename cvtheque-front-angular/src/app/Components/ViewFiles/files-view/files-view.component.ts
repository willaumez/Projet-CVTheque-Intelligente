import {Component, OnInit} from '@angular/core';
import {
  CdkDragDrop,
  moveItemInArray,
  transferArrayItem,
} from '@angular/cdk/drag-drop';
import {AiChatService} from "../../../Services/AiServices/ai-chat.service";
import {FileDB, Folder} from "../../../Models/FileDB";
import {BehaviorSubject, Observable, Subject} from "rxjs";
import {HttpEventType} from "@angular/common/http";
import {ActivatedRoute} from "@angular/router";
import {FoldersService} from "../../../Services/FolderServices/folders.service";
import {Criteria, Profile} from "../../../Models/Profile";
import {FormControl} from "@angular/forms";
import {map, startWith} from "rxjs/operators";
import {COMMA, ENTER} from "@angular/cdk/keycodes";
import {MatChipInputEvent} from "@angular/material/chips";

@Component({
  selector: 'app-files-view',
  templateUrl: './files-view.component.html',
  styleUrl: './files-view.component.scss'
})

export class FilesViewComponent implements OnInit{
  inputValue: string = '';
  isListView: boolean = true;
  isGridView: boolean = false;

  selectedItem: string = 'Criteria';
  criteria$: string[] = [];

  loading = false;
  error: string = '';
  folderId!: number;

  //File
  selectedCriteria: string[] = [];
  jobDescription: string = '';
  files: FileDB[] = [];

  data$: BehaviorSubject<FileDB[]> = new BehaviorSubject<FileDB[]>([]);
  folder!: Folder;

  //profile
  profileCtrl = new FormControl();
  filteredProfiles: Observable<Profile[]>;
  profiles: Profile[] = [];
  editMode: boolean[] = [];
  editValue: string = '';

  //Keywords
  keywords: string[] = [];
  separatorKeysCodes: number[] = [ENTER, COMMA];
  addOnBlur = true;

  constructor(private _aiService: AiChatService, private route: ActivatedRoute, private folderService: FoldersService) {
    this.filteredProfiles = this.profileCtrl.valueChanges.pipe(
      startWith(''),
      map(value => this._filterProfiles(value))
    );
    this.editMode = new Array(this.selectedCriteria.length).fill(false);
  }
  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id !== null) {
      this.folderId = +id;
      this.getFolderById();
    }

    //profile
    this.profiles = this.profilesData;
  }


  toggleListView() {
    this.isListView = true;
    this.isGridView = false;
  }

  toggleGridView() {
    this.isGridView = true;
    this.isListView = false;
  }

  profilesData: Profile[] = [
    {
      id: 1,
      name: 'John Doe',
      description: 'Profile for John Doe',
      createdAt: '2023-08-29T10:00:00Z',
      listCriteria: [
        { name: "5 ans d'expérience en developpement web", message: 'This is the first criteria' },
        { name: "5 ans d'expérience en developpement web5 ans d'expérience en developpement web5 ans d'expérience en developpement web5 ans d'expérience en developpement web5 ans d'expérience en developpement web", message: 'This is the first criteria' },
        { name: "5 ans d'expérience en developpement web", message: 'This is the first criteria' },
        { name: "5 ans d'expérience en developpement web", message: 'This is the first criteria' },
        { name: "5 ans d'expérience en developpement web", message: 'This is the first criteria' },
        { name: "5 ans d'expérience en developpement web", message: 'This is the first criteria' },
        { name: "5 ans d'expérience en developpement web", message: 'This is the first criteria' },
        { name: "posseder un diplome en informatique", message: 'This is the first criteria' },
        { name: "Maitrise des technologies Java, Angular et Spring", message: 'This is the first criteria' },
        { name: "Compétences interpersonnelles (communication, travail en équipe, leadership)", message: 'This is the first criteria' },
        { name: "Résultats et réalisations tangibles mentionnés", message: 'This is the second criteria' }
      ]
    },
    {
      id: 2,
      name: 'Jane Smith',
      description: 'Profile for Jane Smith',
      createdAt: '2023-08-28T09:30:00Z',
      listCriteria: [
        { name: "Adéquation avec la culture d'entreprise", message: 'This is the first criteria' },
        { name: "Clarté et organisation du CV", message: 'This is the second criteria' }
      ]
    },
    {
      id: 3,
      name: 'Alice Johnson',
      description: null, // Description non fournie
      createdAt: '2023-08-27T08:45:00Z',
      listCriteria: [
        { name: "Motivation et qualité de la lettre de motivation", message: 'This is the first criteria' },
        { name: 'Criteria 3', message: 'This is the third criteria' }
      ]
    },
    {
      id: 4,
      name: 'Bob Brown',
      description: 'Profile for Bob Brown',
      createdAt: '2023-08-26T11:20:00Z',
      listCriteria: [
        { name: 'Criteria 2', message: 'This is the second criteria' },
        { name: 'Criteria 3', message: 'This is the third criteria' }
      ]
    },
    {
      id: 5,
      name: 'Emily White',
      description: 'Profile for Emily White',
      createdAt: '2023-08-25T12:15:00Z',
      listCriteria: [
        { name: 'Criteria 1', message: 'This is the first criteria' },
        { name: 'Criteria 4', message: 'This is the fourth criteria' }
      ]
    }
  ];

  private _filterProfiles(value: any): Profile[] {
    if (typeof value === 'string') {
      const filterValue = value.toLowerCase();
      return this.profiles.filter(profile => profile.name.toLowerCase().includes(filterValue));
    } else {
      return this.profiles;
    }
  }


  displayProfile(profile: Profile): string {
    return profile && profile.name ? profile.name : '';
  }
  onFocus(): void {
    this.profileCtrl.setValue(this.profileCtrl.value);
    if (this.profileCtrl.value?.listCriteria){
      this.selectedCriteria = [];
      this.criteria$ = this.profileCtrl.value.listCriteria.map((criterion: { name: any; }) => criterion.name);
    }else {
      this.criteria$ = [];
    }
  }
  enableEdit(index: number, item: string) {
    this.editMode[index] = true;
    this.editValue = item; // Stocker la valeur actuelle pour l'éditer
  }

  saveEdit(index: number) {
    if (this.editValue.trim()) {
      this.selectedCriteria[index] = this.editValue; // Mettre à jour la valeur dans `selectedCriteria`
    }
    this.editMode[index] = false; // Désactiver le mode édition
  }

  cancelEdit(index: number) {
    this.editMode[index] = false; // Désactiver le mode édition sans sauvegarder
  }
  //end profile

  //KeyWords
  addKeyword(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();
    if (value) {
      this.keywords.push(value);
    }
    event.chipInput!.clear();
  }
  removeKeyword(keyword: string): void {
    const index = this.keywords.indexOf(keyword);
    if (index >= 0) {
      this.keywords.splice(index, 1);
    }
  }
  //end keywords


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

  evaluateByCriteria() {
    this.loading = true;
    const criteria = {
      selectedCriteria: this.selectedCriteria,
      jobDescription: this.jobDescription,
      folderId: this.folderId ? this.folderId : undefined
    };

    this._aiService.selectWithCriteria(criteria).subscribe(
      (event: any) => {
        if (event.type === HttpEventType.Response) {
          const response: FileDB[] = JSON.parse(event.body);
          this.data$.next(response);
          this.loading = false;
        }
      },
      (error) => {
        this.error = 'Error fetching files: ' + error.message;
        this.loading = false;
      }
    );
  }
  evaluateKeywords(){
    this.loading = true;
    const keywords = {
      keywords: this.keywords,
      folderId: this.folderId ? this.folderId : undefined
    };
    this._aiService.selectWithKeywords(keywords).subscribe(
      (event: any) => {
        if (event.type === HttpEventType.Response) {
          const response: FileDB[] = JSON.parse(event.body);
          this.data$.next(response);
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
    this.keywords = [];
    this.data$.next([]);
  }

  //protected readonly event = event;
  onIconClick() {
    this.selectedCriteria.push(this.inputValue);
    this.inputValue = '';
  }

  getFolderById() {
    this.loading = true;
    this.folderService.getFolder(this.folderId).subscribe(
      (folder: Folder | null) => {
        if (folder) {
          this.folder = folder;
        } else {
          this.error = 'Folder not found.';
        }
        this.loading = false;
      },
      (error) => {
        this.error = 'Error fetching files: ' + error.message;
        this.loading = false;
      }
    );
  }

  //function
  selectItem(item: string) {
    this.selectedItem = item;
    this.cancelData();
  }


}

