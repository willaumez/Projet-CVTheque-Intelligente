import { Component } from '@angular/core';

@Component({
  selector: 'app-template-screens',
  templateUrl: './template-screens.component.html',
  styleUrl: './template-screens.component.scss'
})
export class TemplateScreensComponent {
  isOpen = true;

  toggleDrawer(drawer: any) {
    this.isOpen = !this.isOpen;
    drawer.toggle();
  }

  onDrawerToggle(opened: boolean) {
    this.isOpen = opened;
  }

}
