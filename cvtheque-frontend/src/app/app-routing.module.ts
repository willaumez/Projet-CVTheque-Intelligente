import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {HomeComponent} from "./Components/home/home.component";
import {LoadFilesComponent} from "./Components/load-files/load-files.component";
import {TemplateScreensComponent} from "./Components/template-screens/template-screens.component";

const routes: Routes = [
  {path: "", redirectTo: "/cvs", pathMatch: "full"},
  {
    path: 'cvs',
    component: TemplateScreensComponent,
    children: [
      {path: "", redirectTo: "home", pathMatch: "full"},
      {path: "home", component: HomeComponent},
      {path: "load-files", component: LoadFilesComponent},
    ],
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
