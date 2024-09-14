import {Component, EventEmitter, OnInit, Output, ViewChild} from '@angular/core';
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
import {ListViewComponent} from "../list-view/list-view.component";
import {GridViewComponent} from "../grid-view/grid-view.component";

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

  folder!: Folder;
  @ViewChild(ListViewComponent) listView!: ListViewComponent;
  @ViewChild(GridViewComponent) gridView!: GridViewComponent;

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

  refreshChildViews() {
    if (this.isListView && this.listView) {
      this.listView.refreshData();
    } else if (this.isGridView && this.gridView) {
      this.gridView.refreshData();
    }
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
        console.log(profiles);
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
    if (this.profileCtrl.value && this.profileCtrl.value.id) {
      this.getCriteria(this.profileCtrl.value.id);
    } else {
      this.criteria$ = [];
    }
  }

  getCriteria(id: number) {
    this._profileService.getCriteriaByProfileId(id).subscribe({
      next: (criteria: CriteriaDB[]) => {
        if (this.profileCtrl.value) {
          this.profileCtrl.value.listCriteria = criteria;
          if (this.profileCtrl.value.listCriteria) {
            this.selectedCriteria = [];
            this.criteria$ = this.profileCtrl.value.listCriteria.map(
              (criteria: { description: any }) => criteria.description
            );
          } else {
            this.criteria$ = [];
          }
        }
      },
      error: (error: Error) => {
        this.error = error.message;
      }
    });
  }

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
    if (this.editValue.trim()) {
      this.selectedCriteria[index] = this.editValue;
    }
    this.editMode[index] = false;
    this.previousEditIndex = null;
  }

  cancelEdit(index: number) {
    this.editMode[index] = false;
    this.previousEditIndex = null;
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

    this._aiService.selectWithCriteria(criteria).subscribe({
      next: (event: any) => {
        if (event.type === HttpEventType.Response) {
          this.refreshChildViews();
          this.cancelData();
          this.loading = false;
        }
      },
      error: (error: Error) => {
        this.error = 'Error fetching files: ' + error.message;
        this.loading = false;
      }
    });
  }
  evaluateKeywords(){
    this.loading = true;
    const keywords = {
      keywords: this.keywords,
      folderId: this.folderId ? this.folderId : undefined
    };
    this._aiService.selectWithKeywords(keywords).subscribe({
      next: (event: any) => {
        if (event.type === HttpEventType.Response) {
          this.refreshChildViews();
          this.cancelData();
          this.loading = false;
        }
      },
      error: (error: Error) => {
        this.error = 'Error fetching files: ' + error.message;
        this.loading = false;
      }
    });
  }
  evaluateScoring() {
    this.loading = true;
    const scoring = {
      jobDescription: this.jobDescription,
      folderId: this.folderId ? this.folderId : undefined
    };
    this._aiService.selectByScoring(scoring).subscribe({
      next: (event: any) => {
        if (event.type === HttpEventType.Response) {
          this.refreshChildViews();
          this.cancelData();
          this.loading = false;
        }
      },
      error: (error: Error) => {
        this.error = 'Error fetching files: ' + error.message;
        this.loading = false;
      }
    });
  }

  cancelData() {
    this.selectedCriteria = [];
    this.jobDescription = '';
    this.keywords = [];
    this.profileCtrl.setValue('');
    this.criteria$ = [];
    this.refreshChildViews();
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

