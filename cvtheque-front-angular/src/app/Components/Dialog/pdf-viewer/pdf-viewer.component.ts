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

  constructor(@Inject(MAT_DIALOG_DATA) public data: any, private _fileService: FilesService) {
  }

  ngOnInit(): void {
    if (this.data) {
      console.log("File Id:    "+this.data);
      //this.pdfSrc = this.data.url;
    }

    this._fileService.readFile(this.data).subscribe(
      (response) => {
        let blob: Blob = response.body as Blob;
        let url = window.URL.createObjectURL(blob);
        this.pdfSrc= url;
        console.log(response);
      }
    );


  }

}
