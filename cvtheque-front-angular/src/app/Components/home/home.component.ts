import {Component, OnInit} from '@angular/core';
import {CVStatsDTO} from "../../Models/FileDB";
import {FilesService} from "../../Services/FileServices/files.service";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {
  chartOptions: any;
  folders!: string[];
  months!: string[];
  cvStats!: CVStatsDTO[];
  year!: number;
  years!: number[];

  constructor(private _fileServices: FilesService) {
  }

  //Without Year
  /*ngOnInit(): void {
    this._fileServices.getCvCountPerMonth().subscribe((data: CVStatsDTO[]) => {
      this.cvStats = data;
      this.folders = [...new Set(data.map(stat => stat.folderName))]; // Liste des dossiers uniques
      this.months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
      this.chartOptions = this.mapDataToChartOptions(); // Assigner ici après avoir mis à jour les données
      console.log("===== Stats Back:  " + JSON.stringify(data, null, 2));
    });
  }

  mapDataToChartOptions(): any {
    return {
      animationEnabled: true,
      title: {
        text: "Nombre de CVs par Mois et par Dossier"
      },
      axisX: {
        title: "Mois",
        valueFormatString: "MMM"
      },
      axisY: {
        title: "Nombre de CVs",
        includeZero: true
      },
      toolTip: {
        shared: true
      },
      legend: {
        cursor: "pointer",
        itemclick: function(e: any) {
          if (typeof(e.dataSeries.visible) === "undefined" || e.dataSeries.visible) {
            e.dataSeries.visible = false;
          } else {
            e.dataSeries.visible = true;
          }
          e.chart.render();
        }
      },
      data: this.folders.map(folder => ({
        type: "spline",
        showInLegend: true,
        name: folder,
        dataPoints: this.months.map((month, index) => ({
          label: month,
          y: this.cvStats.find(stat => stat.folderName === folder && stat.month === index + 1)?.count || 0
        }))
      }))
    };
  }*/

  //With Year
 /* ngOnInit(): void {
    this._fileServices.getCvCountPerMonth().subscribe((data: CVStatsDTO[]) => {
      this.cvStats = data;
      if (data.length > 0) {
        this.year = data[0].year; // Supposer que toutes les données ont la même année
      }
      this.folders = [...new Set(data.map(stat => stat.folderName))]; // Liste des dossiers uniques
      this.months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
      this.chartOptions = this.mapDataToChartOptions(); // Assigner ici après avoir mis à jour les données
      console.log("===== Stats Back:  " + JSON.stringify(data, null, 2));
    });
  }

  mapDataToChartOptions(): any {
    return {
      animationEnabled: true,
      title: {
        text: `Nombre de CVs par Mois et par Dossier (${this.year})`
      },
      axisX: {
        title: "Mois",
        valueFormatString: "MMM"
      },
      axisY: {
        title: "Nombre de CVs",
        includeZero: true
      },
      toolTip: {
        shared: true
      },
      legend: {
        cursor: "pointer",
        itemclick: function (e: any) {
          if (typeof (e.dataSeries.visible) === "undefined" || e.dataSeries.visible) {
            e.dataSeries.visible = false;
          } else {
            e.dataSeries.visible = true;
          }
          e.chart.render();
        }
      },
      data: this.folders.map(folder => ({
        type: "spline",
        showInLegend: true,
        name: folder,
        dataPoints: this.months.map((month, index) => ({
          label: `${month} ${this.year}`,
          y: this.cvStats.find(stat => stat.folderName === folder && stat.month === index + 1)?.count || 0
        }))
      }))
    };
  }
*/


  ngOnInit(): void {
    this._fileServices.getCvCountPerMonth().subscribe((data: CVStatsDTO[]) => {
      this.cvStats = data;
      this.years = [...new Set(data.map(stat => stat.year))]; // Liste des années uniques
      this.folders = [...new Set(data.map(stat => stat.folderName))]; // Liste des dossiers uniques
      this.months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
      this.chartOptions = this.mapDataToChartOptions(); // Assigner ici après avoir mis à jour les données
      console.log("===== Stats Back:  " + JSON.stringify(data, null, 2));
    });
  }

  mapDataToChartOptions(): any {
    return {
      animationEnabled: true,
      title: {
        text: "Nombre de CVs par Mois et par Dossier"
      },
      axisX: {
        title: "Mois",
        valueFormatString: "MMM"
      },
      axisY: {
        title: "Nombre de CVs",
        includeZero: true
      },
      toolTip: {
        shared: true
      },
      legend: {
        cursor: "pointer",
        itemclick: function(e: any) {
          if (typeof(e.dataSeries.visible) === "undefined" || e.dataSeries.visible) {
            e.dataSeries.visible = false;
          } else {
            e.dataSeries.visible = true;
          }
          e.chart.render();
        }
      },
      data: this.folders.flatMap(folder =>
        this.years.map(year => ({
          type: "spline",
          showInLegend: true,
          name: `${folder} (${year})`,
          dataPoints: this.months.map((month, index) => ({
            label: `${month}`,
            y: this.cvStats.find(stat =>
              stat.folderName === folder &&
              stat.year === year &&
              stat.month === index + 1
            )?.count || 0
          }))
        }))
      )
    };
  }

  chartOptions2 = {
    animationEnabled: true,
    title: {
      text: "Project Cost Breakdown"
    },
    data: [{
      type: "doughnut",
      yValueFormatString: "#,###.##'%'",
      indexLabel: "{name}",
      dataPoints: [
        {y: 28, name: "Labour"},
        {y: 10, name: "Legal"},
        {y: 20, name: "Production"},
        {y: 15, name: "License"},
        {y: 23, name: "Facilities"},
        {y: 17, name: "Taxes"},
        {y: 12, name: "Insurance"}
      ]
    }]
  }
  chartOptions3 = {
    animationEnabled: true,
    title: {
      text: "Sales by Department"
    },
    data: [{
      type: "pie",
      startAngle: -90,
      indexLabel: "{name}: {y}",
      yValueFormatString: "#,###.##'%'",
      dataPoints: [
        {y: 14.1, name: "Toys"},
        {y: 28.2, name: "Electronics"},
        {y: 14.4, name: "Groceries"},
        {y: 43.3, name: "Furniture"}
      ]
    }]
  }


  protected readonly JSON = JSON;
}
