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
import {CriteriaDB, Profile} from "../../../Models/Profile";
import {FormControl} from "@angular/forms";
import {map, startWith} from "rxjs/operators";
import {COMMA, ENTER} from "@angular/cdk/keycodes";
import {MatChipInputEvent} from "@angular/material/chips";
import {ProfileService} from "../../../Services/ProfileServices/profile.service";

@Component({
  selector: 'app-files-view',
  templateUrl: './files-view.component.html',
  styleUrl: './files-view.component.scss'
})

export class FilesViewComponent implements OnInit{
  inputValue: string = '';
  isListView: boolean = false;
  isGridView: boolean = true;

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
  previousEditIndex: number | null = null;
  editValue: string = '';

  //Keywords
  keywords: string[] = [];
  separatorKeysCodes: number[] = [ENTER, COMMA];
  addOnBlur = true;

  constructor(private _aiService: AiChatService, private _profileService : ProfileService, private route: ActivatedRoute, private folderService: FoldersService) {
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
    this.getAllProfiles();
  }

  getAllProfiles() {
    this.loading = true;
    this._profileService.getAllProfiles().subscribe({
      next: (profiles: Profile[]) => {
        this.profiles = profiles;
      },
      error: (error: Error) => {
        this.error = error.message;
      },
      complete: () => {
        this.loading = false;
      }
    });
  }



  toggleListView() {
    this.isListView = true;
    this.isGridView = false;
  }

  toggleGridView() {
    this.isGridView = true;
    this.isListView = false;
  }

  /*profilesData: Profile[] = [
    {
      id: 1,
      name: 'Développeur Web Senior',
      description: 'Poste pour un Développeur Web Senior avec 5 ans d\'expérience',
      createdAt: '2023-08-29T10:00:00Z',
      listCriteria: [
        { id: 1, description: "5 ans d'expérience en développement web" },
        { id: 2, description: "Maîtrise des technologies Java, Angular et Spring" },
        { id: 3, description: "Compétences interpersonnelles (communication, travail en équipe, leadership)" },
        { id: 4, description: "Posséder un diplôme en informatique" },
        { id: 5, description: "Résultats et réalisations tangibles mentionnés dans le CV" },
        { id: 14, description: "Expérience avec les API RESTful" },
        { id: 15, description: "Connaissance des bases de données SQL et NoSQL" },
        { id: 16, description: "Expérience en déploiement continu et intégration continue (CI/CD)" }
      ]
    },
    {
      id: 2,
      name: 'Responsable RH',
      description: 'Poste pour un Responsable des Ressources Humaines',
      createdAt: '2023-08-28T09:30:00Z',
      listCriteria: [
        { id: 6, description: "Adéquation avec la culture d'entreprise" },
        { id: 7, description: "Clarté et organisation du CV" },
        { id: 17, description: "Compétences en gestion des conflits" },
        { id: 18, description: "Expérience en recrutement et formation" },
        { id: 19, description: "Connaissance des lois du travail" }
      ]
    },
    {
      id: 3,
      name: 'Chef de Projet IT',
      description: 'Poste pour un Chef de Projet IT',
      createdAt: '2023-08-27T08:45:00Z',
      listCriteria: [
        { id: 8, description: "Motivation et qualité de la lettre de motivation" },
        { id: 9, description: "Expérience en gestion de projets IT" },
        { id: 20, description: "Compétences en gestion de budget" },
        { id: 21, description: "Capacité à coordonner des équipes multi-disciplinaires" },
        { id: 22, description: "Expérience avec les méthodologies Agile et Scrum" },
        { id: 23, description: "Compétences en gestion des risques de projet" }
      ]
    },
    {
      id: 4,
      name: 'Analyste Fonctionnel',
      description: 'Poste pour un Analyste Fonctionnel',
      createdAt: '2023-08-26T11:20:00Z',
      listCriteria: [
        { id: 10, description: "Connaissance des méthodologies Agile" },
        { id: 11, description: "Expérience avec les outils de modélisation" },
        { id: 24, description: "Capacité à analyser les besoins des clients" },
        { id: 25, description: "Expérience en rédaction de spécifications fonctionnelles" },
        { id: 26, description: "Connaissance des processus métier" },
        { id: 27, description: "Compétences en communication pour la traduction des besoins techniques en termes non-techniques" }
      ]
    },
    {
      id: 5,
      name: 'Ingénieur DevOps',
      description: 'Poste pour un Ingénieur DevOps',
      createdAt: '2023-08-25T12:15:00Z',
      listCriteria: [
        { id: 12, description: "Expérience avec les pipelines CI/CD" },
        { id: 13, description: "Maîtrise des outils de conteneurisation comme Docker et Kubernetes" },
        { id: 28, description: "Expérience en automatisation des processus" },
        { id: 29, description: "Connaissance des systèmes de gestion de configuration (Ansible, Puppet, Chef)" },
        { id: 30, description: "Compétences en surveillance et gestion des performances" },
        { id: 31, description: "Capacité à résoudre des problèmes complexes en production" }
      ]
    }
  ];*/

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
      this.criteria$ = this.profileCtrl.value.listCriteria.map((criterion: { description: any; }) => criterion.description);
    }else {
      this.criteria$ = [];
    }
  }

  /*enableEdit(index: number, item: string) {
    if(this.editValue){
      this.saveEdit(index);
    }
    this.editMode[index] = true;
    this.editValue = item;
  }

  saveEdit(index: number) {
    if (this.editValue.trim()) {
      this.selectedCriteria[index] = this.editValue; // Mettre à jour la valeur dans `selectedCriteria`
    }
    this.editMode[index] = false; // Désactiver le mode édition
  }

  cancelEdit(index: number) {
    this.editMode[index] = false; // Désactiver le mode édition sans sauvegarder
  }*/

  enableEdit(index: number, item: string) {
    // Si un index précédent existe et que l'édition est active, sauvegarder la valeur précédente
    if (this.previousEditIndex !== null && this.editValue) {
      this.saveEdit(this.previousEditIndex);
    }

    // Activer l'édition pour le nouvel index
    this.editMode[index] = true;
    this.editValue = item;
    this.previousEditIndex = index; // Mettre à jour l'index précédent
  }

  saveEdit(index: number) {
    // Vérifier si la valeur éditée n'est pas vide
    if (this.editValue.trim()) {
      this.selectedCriteria[index] = this.editValue; // Mettre à jour la valeur dans `selectedCriteria`
    }
    this.editMode[index] = false; // Désactiver le mode édition
    this.previousEditIndex = null; // Réinitialiser l'index précédent après la sauvegarde
  }

  cancelEdit(index: number) {
    this.editMode[index] = false; // Désactiver le mode édition sans sauvegarder
    this.previousEditIndex = null; // Réinitialiser l'index précédent
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
  evaluateScoring() {
    this.loading = true;
    const scoring = {
      jobDescription: this.jobDescription,
      folderId: this.folderId ? this.folderId : undefined
    };
    this._aiService.selectByScoring(scoring).subscribe(
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
    this.profileCtrl.setValue('');
    this.criteria$ = [];
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

