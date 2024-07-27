import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { HomeComponent } from './Components/home/home.component';
import {MatDrawer, MatSidenavModule} from "@angular/material/sidenav";
import {MatListItem, MatNavList} from "@angular/material/list";
import {MatIcon} from "@angular/material/icon";
import {MatDivider} from "@angular/material/divider";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatTooltip} from "@angular/material/tooltip";
import { LoadFilesComponent } from './Components/load-files/load-files.component';
import { TemplateScreensComponent } from './Components/template-screens/template-screens.component';
import {MatButtonToggle} from "@angular/material/button-toggle";


import { LoadingBarRouterModule } from '@ngx-loading-bar/router';
import { LoadingBarModule } from '@ngx-loading-bar/core';
import {NgxDropzoneModule} from "ngx-dropzone";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { MatFormFieldModule} from "@angular/material/form-field";
import { MatAutocompleteModule} from "@angular/material/autocomplete";
import { MatInputModule} from "@angular/material/input";
import {MatSlideToggleModule} from "@angular/material/slide-toggle";
import {AsyncPipe, NgOptimizedImage} from "@angular/common";
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import {MatProgressBar} from "@angular/material/progress-bar";
import { AddFolderComponent } from './Components/Dialog/add-folder/add-folder.component';
import {MatDialogActions, MatDialogClose, MatDialogContent, MatDialogTitle} from "@angular/material/dialog";
import {FilesViewComponent} from "./Components/ViewFiles/files-view/files-view.component";
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow, MatHeaderRowDef, MatNoDataRow, MatRow, MatRowDef,
  MatTable
} from "@angular/material/table";
import {MatCheckbox} from "@angular/material/checkbox";
import { ListViewComponent } from './Components/ViewFiles/list-view/list-view.component';
import { GridViewComponent } from './Components/ViewFiles/grid-view/grid-view.component';
import {FlexLayoutModule} from "@angular/flex-layout";
import {MatToolbar} from "@angular/material/toolbar";
import {MatCardModule} from "@angular/material/card";


@NgModule({
  declarations: [
    AppComponent,
    TemplateScreensComponent,
    HomeComponent,
    LoadFilesComponent,
    AddFolderComponent,
    FilesViewComponent,
    ListViewComponent,
    GridViewComponent
  ],
  imports: [
    BrowserModule, AppRoutingModule, BrowserModule, AppRoutingModule,
    MatSidenavModule, MatDrawer, MatNavList, MatListItem, MatIcon, MatDivider, MatButton, MatIconButton, MatTooltip,
    MatButtonToggle, LoadingBarRouterModule, LoadingBarModule, NgxDropzoneModule, FormsModule, MatFormFieldModule,
    MatInputModule, MatAutocompleteModule, ReactiveFormsModule, MatSlideToggleModule, AsyncPipe,
    NgbModule, MatProgressBar, MatDialogContent, MatDialogActions, MatDialogTitle, MatDialogClose, MatTable,
    MatColumnDef, MatHeaderCell, MatCheckbox, MatCell, MatCellDef, MatHeaderCellDef, MatHeaderRow, MatRow,
    MatRowDef, MatHeaderRowDef, MatNoDataRow, FlexLayoutModule, MatToolbar, MatCardModule, NgOptimizedImage,

  ],
  providers: [
    provideAnimationsAsync()
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
