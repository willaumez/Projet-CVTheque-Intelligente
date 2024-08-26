import {Component, OnInit} from '@angular/core';
import {FileDB, Folder} from "../../../Models/FileDB";
import {SelectionModel} from "@angular/cdk/collections";
import {FoldersService} from "../../../Services/FolderServices/folders.service";

@Component({
  selector: 'app-folder-view',
  templateUrl: './folder-view.component.html',
  styleUrl: './folder-view.component.scss'
})
export class FolderViewComponent implements OnInit{
  isChecked = false;
  gridColumns = 10;
  dataSource: Folder[] = [];
  error: string = '';
  selection = new SelectionModel<number>(true, []);


  ngOnInit(): void {
    this.getAllFolders();
  }
  constructor(private folderService: FoldersService) {
  }


  //Redirect to the folder content
  handleCardClick(folder: Folder) {
    console.log(`Card ${folder} clicked`);
  }
  //Select folder
  toggleSelection(folderId: number) {
    this.selection.toggle(folderId);
    //console.log(this.selection.selected);
  }
  checkboxLabel(row?: number): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row}`;
  }

  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.length;
    return numSelected === numRows;
  }

  //Get all folders
  getAllFolders() {
    this.folderService.getAllFolders().subscribe({
      next: (folders: Folder[]) => {
        this.dataSource = folders;
        //console.log('Folders fetched:', folders);
      },
      error: (error) => {
        this.error = 'Error fetching folders: ' + error.message;
      }
    });
  }


}
