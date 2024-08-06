import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {LoadFilesComponent} from "./Components/load-files/load-files.component";
import {HomeComponent} from "./Components/home/home.component";
import {TemplateScreensComponent} from "./Components/template-screens/template-screens.component";
import {FilesViewComponent} from "./Components/ViewFiles/files-view/files-view.component";
import {ChatAIComponent} from "./Components/chat-ai/chat-ai.component";


const routes: Routes = [
  {path: "", redirectTo: "/cvs", pathMatch: "full"},
  {
    path: 'cvs',
    component: TemplateScreensComponent,
    children: [
      {path: "", redirectTo: "home", pathMatch: "full"},
      {path: "home", component: HomeComponent},
      {path: "files-view", component: FilesViewComponent},
      {path: "files-view/chat", component: ChatAIComponent},
      {path: "load-files", component: LoadFilesComponent},
    ],
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
