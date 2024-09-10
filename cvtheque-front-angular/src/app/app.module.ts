import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {provideAnimationsAsync} from '@angular/platform-browser/animations/async';
import {HomeComponent} from './Components/home/home.component';
import {MatDrawer, MatSidenavModule} from "@angular/material/sidenav";
import {MatListItem, MatListModule, MatNavList} from "@angular/material/list";
import {MatIcon} from "@angular/material/icon";
import {MatDivider} from "@angular/material/divider";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatTooltip, MatTooltipModule} from "@angular/material/tooltip";
import {LoadFilesComponent} from './Components/load-files/load-files.component';
import {TemplateScreensComponent} from './Components/template-screens/template-screens.component';
import {MatButtonToggle} from "@angular/material/button-toggle";


import {LoadingBarRouterModule} from '@ngx-loading-bar/router';
import {LoadingBarModule} from '@ngx-loading-bar/core';
import {NgxDropzoneModule} from "ngx-dropzone";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatAutocompleteModule} from "@angular/material/autocomplete";
import {MatInputModule} from "@angular/material/input";
import {MatSlideToggleModule} from "@angular/material/slide-toggle";
import {AsyncPipe, NgOptimizedImage} from "@angular/common";
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {MatProgressBar} from "@angular/material/progress-bar";
import {AddFolderComponent} from './Components/Dialog/add-folder/add-folder.component';
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
import {ListViewComponent} from './Components/ViewFiles/list-view/list-view.component';
import {GridViewComponent} from './Components/ViewFiles/grid-view/grid-view.component';
import {FlexLayoutModule} from "@angular/flex-layout";
import {MatToolbar} from "@angular/material/toolbar";
import {MatCardModule} from "@angular/material/card";
import {HttpClientModule} from "@angular/common/http";
import {DragDropModule} from "@angular/cdk/drag-drop";
import {ChatAIComponent} from './Components/chat-ai/chat-ai.component';
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {NgxExtendedPdfViewerModule} from 'ngx-extended-pdf-viewer';
import {PdfViewerComponent} from './Components/Dialog/pdf-viewer/pdf-viewer.component';
import {MatSelect} from "@angular/material/select";
import {MatLine} from "@angular/material/core";
import {MatSort, MatSortModule} from "@angular/material/sort";
import { FolderViewComponent } from './Components/ViewFiles/folder-view/folder-view.component';
import {MatMenuModule} from "@angular/material/menu";
import { DeleteFolderComponent } from './Components/Dialog/delete-folder/delete-folder.component';
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatExpansionModule} from "@angular/material/expansion";
import {MatChipsModule} from "@angular/material/chips";
import { SettingsComponent } from './Components/Settings/settings/settings.component';
import {MatTabsModule} from "@angular/material/tabs";
import { ProfileCriteriaComponent } from './Components/Settings/profile-criteria/profile-criteria.component';
import { UserManagementComponent } from './Components/Settings/user-management/user-management.component';
import { AddProfileComponent } from './Components/Dialog/add-profile/add-profile.component';
import {MatSnackBarModule} from "@angular/material/snack-bar";
import {CanvasJSAngularChartsModule} from "@canvasjs/angular-charts";


@NgModule({
  declarations: [
    AppComponent,
    TemplateScreensComponent,
    HomeComponent,
    LoadFilesComponent,
    AddFolderComponent,
    FilesViewComponent,
    ListViewComponent,
    GridViewComponent,
    ChatAIComponent,
    PdfViewerComponent,
    FolderViewComponent,
    DeleteFolderComponent,
    SettingsComponent,
    ProfileCriteriaComponent,
    UserManagementComponent,
    AddProfileComponent,
  ],
  imports: [
    BrowserModule, AppRoutingModule, MatSidenavModule, MatIcon, MatDivider, MatButton, MatTooltip, MatButtonToggle,
    LoadingBarRouterModule, LoadingBarModule, NgxDropzoneModule, FormsModule, MatFormFieldModule, MatInputModule,
    MatAutocompleteModule, ReactiveFormsModule, MatSlideToggleModule, AsyncPipe, NgbModule, MatProgressBar, MatDialogContent,
    MatDialogActions, MatDialogTitle, MatDialogClose, MatTable, MatColumnDef, MatHeaderCell, MatCheckbox, MatCell, MatCellDef, MatHeaderCellDef,
    MatHeaderRow, MatRow, MatRowDef, MatHeaderRowDef, MatNoDataRow, FlexLayoutModule, MatToolbar, MatCardModule, NgOptimizedImage,
    HttpClientModule, MatListItem, MatNavList, DragDropModule, MatProgressSpinnerModule, NgxExtendedPdfViewerModule, MatSelect,
    MatListModule, MatLine, MatSortModule, MatIconButton,MatMenuModule,MatPaginatorModule, MatExpansionModule,MatChipsModule,
    MatTabsModule, MatTooltipModule, CanvasJSAngularChartsModule
  ],
  providers: [
    provideAnimationsAsync()
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
