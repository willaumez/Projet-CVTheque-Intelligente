import {Component, OnInit} from '@angular/core';
import { Folder} from "../../../Models/FileDB";
import {SelectionModel} from "@angular/cdk/collections";
import {FoldersService} from "../../../Services/FolderServices/folders.service";
import {AddFolderComponent} from "../../Dialog/add-folder/add-folder.component";
import {MatDialog} from "@angular/material/dialog";
import {DeleteFolderComponent} from "../../Dialog/delete-folder/delete-folder.component";
import {Router} from "@angular/router";

@Component({
  selector: 'app-folder-view',
  templateUrl: './folder-view.component.html',
  styleUrl: './folder-view.component.scss'
})
export class FolderViewComponent implements OnInit {
  isChecked = false;
  dataSource: Folder[] = [];
  error: string = '';
  isLoading = false;
  selection = new SelectionModel<number>(true, []);
  keyword: string = "";
  private transDelet: boolean = false;


  ngOnInit(): void {
    this.getAllFolders();
  }

  constructor(private folderService: FoldersService, private router: Router, private _dialog: MatDialog) {
  }


  //Redirect to the folder content
  handleCardClick(id: number) {
    this.router.navigate([`/cvs/folders/files/`, id]);
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

  toggleAllGrid() {
    if (this.isAllSelected()) {
      this.selection.clear();
      return;
    }
    this.selection.select(...this.dataSource.map(num => num.id));
  }

  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.length;
    return numSelected === numRows;
  }

  //Recherche
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.keyword = filterValue.trim();
    this.getAllFolders();
  }

  //Get all folders
  getAllFolders() {
    this.isLoading = true;
    this.error = '';
    this.folderService.getAllFolders(this.keyword).subscribe({
      next: (folders: Folder[]) => {
        this.dataSource = folders;
        this.isLoading = false;
      },
      error: (error) => {
        this.error = 'Error fetching folders: ' + error.message;
        this.isLoading = false;
      }
    });
  }

  //Reset selection
  checkedClick() {
    if (this.isChecked) {
      this.selection.clear();
    }
  }

  //Delete selected folders
  deleteSelection() {
    this.error = '';
    const data: {} = {
      deleteSelection: true,
      selection: this.selection.selected,
    }
    const dialogRef = this._dialog.open(DeleteFolderComponent, {
      data,
    });
    dialogRef.afterClosed().subscribe((val) => {
        this.getAllFolders();
        this.selection.clear();
      },
    );
  }

  //Rename folder
  rename(folder: Folder) {
    this.error = '';
    const data: any = folder
    const dialogRef = this._dialog.open(AddFolderComponent, {
      data,
    });
    dialogRef.afterClosed().subscribe((val: Folder) => {
      //console.log('Dialog closed with value: ', val);
        if (val) {
          this.getAllFolders();
        }
      },
    );
  }

  //Delete folder
  deleteAllFiles(id: number) {
    const data: {} = {
      deleteFolder: true,
      folderId: id,
    }
    const dialogRef = this._dialog.open(DeleteFolderComponent, {
      data,
    });
    dialogRef.afterClosed().subscribe((val: Folder) => {
        if (val) {
          this.getAllFolders();
        }
      },
    );
  }

  transfer(id: number) {
    const data: {} = {
      transferFromFolder: true,
      folderId: id,
    }
    const dialogRef = this._dialog.open(AddFolderComponent, {
      data,
    });
    dialogRef.afterClosed().subscribe((val: Folder) => {
        if (val) {
          this.getAllFolders();
          if (this.transDelet) {
            this.delete(id);
            this.transDelet = false;
          }
        }
      },
    );
  }

  transferToDelete(id: number) {
    this.transfer(id);
    this.transDelet = true;
  }

  delete(id: number) {
    this.folderService.delete(id).subscribe({
      next: (val) => {
        this.getAllFolders();
      },
      error: (error) => {
        console.error('Error deleting folder:', error);
      }
    });
  }

  openAddFolder() {
    const dialogRef = this._dialog.open(AddFolderComponent, {});
    dialogRef.afterClosed().subscribe((val: Folder) => {
        this.getAllFolders();
      },
    );
  }



}
