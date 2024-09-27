import {Component, Inject, OnInit} from '@angular/core';
import {FoldersService} from "../../../Services/FolderServices/folders.service";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-delete-folder',
  templateUrl: './delete-folder.component.html',
  styleUrl: './delete-folder.component.scss'
})
export class DeleteFolderComponent implements OnInit {
  folderId: number | null = null;
  selection: number[] = [];
  error: string = '';
  inOperation: boolean = false;


  constructor(private folderService: FoldersService,
              private _dialogRef: MatDialogRef<DeleteFolderComponent>, @Inject(MAT_DIALOG_DATA) public data: any) {
  }

  ngOnInit(): void {
    if (this.data.deleteFolder) {
      this.folderId = this.data.folderId;
    } else if (this.data.deleteSelection) {
      this.selection = this.data.selection;
    }
  }

  deleteSubmit() {
    if (this.folderId) {
      this.folderService.delete(this.folderId).subscribe(
        (response) => {
          this._dialogRef.close(response);
        },
        (error) => {
          this.error = 'Error Deleting Folder: ' + error.message;
        }
      );
    } else {
      this.error = 'No folder ID provided.';
    }
  }


  deleteSelection() {
    this.inOperation = true;
      this.folderService.deleteAll(this.selection).subscribe(
        (response) => {
          this.inOperation = false;
          this._dialogRef.close();
        },
        (error) => {
          this.error = 'Error Deleting Folder: ' + error.message;
        }
      );
    this.inOperation = false;
  }


}
