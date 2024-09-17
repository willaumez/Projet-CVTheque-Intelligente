import {Component, OnInit} from '@angular/core';
import {CVStatsDTO, GraphData, HeaderData} from "../../Models/FileDB";
import {FilesService} from "../../Services/FileServices/files.service";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {
  chartOptions: any;
  chartOptions3: any;
  folders!: string[];
  months!: string[];
  graphData!: GraphData[];
  headerData!: HeaderData;
  years!: number[];

  constructor(private _fileServices: FilesService) {}

  ngOnInit(): void {
    this._fileServices.getAllHomeDta().subscribe((data: CVStatsDTO) => {
      this.graphData = data.graphData;
      this.headerData = data.headerData;

      this.years = [...new Set(data.graphData.map(stat => stat.year))]; // Liste des années uniques
      this.folders = [...new Set(data.graphData.map(stat => stat.folderName))]; // Liste des dossiers uniques
      this.months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
      this.chartOptions = this.mapDataToChartOptions(); // Assigner ici après avoir mis à jour les données

      this.chartOptions3 = this.getChartOptions3(); // Mettre à jour chartOptions3 ici
      console.log(this.chartOptions3, null, 2);
    });
  }

  mapDataToChartOptions(): any {
    return {
      animationEnabled: true,
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
            y: this.graphData.find(stat =>
              stat.folderName === folder &&
              stat.year === year &&
              stat.month === index + 1
            )?.count || 0
          }))
        }))
      )
    };
  }


  getChartOptions3(): any {
    if (this.headerData && this.headerData?.totalFiles) {
      let evaluatedPercentage = Math.round((this.headerData.totalEvaluated / this.headerData.totalFiles) * 100);
      let notEvaluatedPercentage = Math.round((this.headerData.totalNotEvaluated / this.headerData.totalFiles) * 100);

      return {
        animationEnabled: true,
        data: [{
          type: "pie",
          startAngle: -90,
          yValueFormatString: "#,###.##'%'",
          indexLabel: "{name}: {y}%",
          dataPoints: [
            { y: evaluatedPercentage, name: "Evaluated" },
            { y: notEvaluatedPercentage, name: "Not Evaluated" }
          ]
        }]
      };
    }
  }

  chartOptions2 = {
    animationEnabled: true,
    title:{
      text: "Project Cost Breakdown"
    },
    data: [{
      type: "pie",
      yValueFormatString: "#,###.##'%'",
      indexLabel: "{name}",
      dataPoints: [
        { y: 28, name: "Labour" },
        { y: 10, name: "Legal" },
        { y: 20, name: "Production" },
        { y: 15, name: "License" },
        { y: 23, name: "Facilities" },
        { y: 17, name: "Taxes" },
        { y: 12, name: "Insurance" }
      ]
    }]
  }

  protected readonly Math = Math;
}

