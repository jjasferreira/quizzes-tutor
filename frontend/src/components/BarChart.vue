<template>
  <div>
    <Bar :chart-data="chartData" :options="chartOptions" ref="chart" />
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import { Bar } from 'vue-chartjs/legacy';
import {
  BarElement,
  CategoryScale,
  Chart as ChartJS,
  ChartData,
  ChartOptions,
  Legend,
  LinearScale,
  Title,
  Tooltip,
} from 'chart.js';

ChartJS.register(
  BarElement,
  CategoryScale,
  Legend,
  LinearScale,
  Title,
  Tooltip
);

@Component({
  components: { Bar },
})
export default class BarChart extends Vue {
  @Prop() readonly labels!: string[];
  @Prop() readonly label1!: string;
  @Prop() readonly data1!: number[];
  @Prop() readonly label2!: string;
  @Prop() readonly data2!: number[];
  @Prop() readonly label3!: string;
  @Prop() readonly data3!: number[];
  @Prop() readonly colors!: string[];

  chartData!: ChartData;
  async created() {
    this.chartData = {
      labels: this.labels,
      datasets: [
        {
          label: this.label1,
          backgroundColor: this.colors[0],
          data: this.data1,
        },
        {
          label: this.label2,
          backgroundColor: this.colors[1],
          data: this.data2,
        },
        {
          label: this.label3,
          backgroundColor: this.colors[2],
          data: this.data3,
        },
      ],
    };
  }

  chartOptions: ChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
  };
}
</script>
