import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoadFilesComponent} from "./Components/load-files/load-files.component";
import {HomeComponent} from "./Components/home/home.component";
import {TemplateScreensComponent} from "./Components/template-screens/template-screens.component";
import {FilesViewComponent} from "./Components/ViewFiles/files-view/files-view.component";
import {ChatAIComponent} from "./Components/chat-ai/chat-ai.component";
import {FolderViewComponent} from "./Components/ViewFiles/folder-view/folder-view.component";
import {SettingsComponent} from "./Components/Settings/settings/settings.component";
import {UserManagementComponent} from "./Components/Settings/user-management/user-management.component";
import {ProfileCriteriaComponent} from "./Components/Settings/profile-criteria/profile-criteria.component";


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
      {path: "folders", component: FolderViewComponent},
      {path: "folders/files/:id", component: FilesViewComponent},
      {path: "load-files", component: LoadFilesComponent},
      {
        path: "settings",
        component: SettingsComponent,
        children: [
          { path: '', redirectTo: 'profile', pathMatch: 'full' },
          {path: "profile", component: ProfileCriteriaComponent},
          {path: "users", component: UserManagementComponent}
        ]
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
