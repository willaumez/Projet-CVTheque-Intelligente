import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {FilesService} from "../../../Services/FileServices/files.service";

@Component({
  selector: 'app-pdf-viewer',
  templateUrl: './pdf-viewer.component.html',
  styleUrl: './pdf-viewer.component.scss'
})
export class PdfViewerComponent implements OnInit{
  pdfSrc = '';
  isLoading = false;
  error: string = '';

  constructor(@Inject(MAT_DIALOG_DATA) public data: any, private _fileService: FilesService) {
  }

  ngOnInit(): void {
    if (this.data) {
      this.getFile(this.data);
    }
  }

  getFile(data:any){
    this.isLoading = false;
    this._fileService.readFile(this.data).subscribe(
      (response) => {
        let blob: Blob = response.body as Blob;
        let url = window.URL.createObjectURL(blob);
        this.pdfSrc= url;

        this.isLoading = false;
      }, (error) => {
        this.error = 'Error fetching files ' + error.message;
        this.isLoading = false;
      }

    );
  }

}
