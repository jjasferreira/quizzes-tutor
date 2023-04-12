<template>
  <div v-if="questionStats.length > 0">
    <bar-chart
      :labels="labelsArray"
      :label1="numQuestionsLabel"
      :data1="numQuestionsArray"
      :label2="numUniqueQuestionsLabel"
      :data2="numUniqueQuestionsArray"
      :label3="numAverageQuestionsLabel"
      :data3="numAverageQuestionsArray"
      :colors="colors"
    ></bar-chart>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import QuestionStats from '@/models/dashboard/QuestionStats';
import BarChart from '@/components/BarChart.vue';

@Component({
  components: { BarChart },
})
export default class TeacherGraphsView extends Vue {
  @Prop() readonly questionStats!: QuestionStats[];

  labelsArray!: string[];
  numQuestionsLabel: string = 'Total Available';
  numQuestionsArray!: number[];
  numUniqueQuestionsLabel: string = 'Solved (Unique)';
  numUniqueQuestionsArray!: number[];
  numAverageQuestionsLabel: string = 'Solved (Unique, Average per Student)';
  numAverageQuestionsArray!: number[];
  colors: string[] = ['#c0392b', '#2980b9', '#1abc9c'];

  async created() {
    this.labelsArray = this.questionStats
      .map((questionStats: QuestionStats) => questionStats.courseExecutionYear.toString())
      .reverse();

    if (this.labelsArray.length > 0) {
      this.labelsArray[this.labelsArray.length - 1] += ' (current)';
    }

    this.numQuestionsArray = this.questionStats
      .map((questionStats: QuestionStats) => questionStats.numAvailable)
      .reverse();

    this.numUniqueQuestionsArray = this.questionStats
      .map((questionStats: QuestionStats) => questionStats.answeredQuestionsUnique)
      .reverse();

    this.numAverageQuestionsArray = this.questionStats
      .map((questionStats: QuestionStats) => questionStats.averageQuestionsAnswered)
      .reverse();
  }
}
</script>
