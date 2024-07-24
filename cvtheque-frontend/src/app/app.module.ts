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

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    LoadFilesComponent,
    TemplateScreensComponent
  ],
  imports: [
    BrowserModule, AppRoutingModule,
    MatSidenavModule, MatDrawer, MatNavList, MatListItem, MatIcon, MatDivider, MatButton, MatIconButton, MatTooltip,
    MatButtonToggle,
    LoadingBarRouterModule, LoadingBarModule,
    NgxDropzoneModule,
  ],
  providers: [
    provideAnimationsAsync(),
    //importProvidersFrom(LoadingBarHttpClientModule),
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
