<template>
  <div v-if="quizStats.length > 0">
    <bar-chart
      :labels="labelsArray"
      :label1="numQuizzesLabel"
      :data1="numQuizzesArray"
      :label2="numUniqueAnsweredQuizzesLabel"
      :data2="numUniqueAnsweredQuizzesArray"
      :label3="numAverageQuizzesLabel"
      :data3="numAverageQuizzesArray"
      :colors="colors"
    ></bar-chart>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import QuizStats from '@/models/dashboard/QuizStats';
import BarChart from '@/components/BarChart.vue';

@Component({
  components: { BarChart },
})
export default class TeacherGraphsView extends Vue {
  @Prop() readonly quizStats!: QuizStats[];

  labelsArray!: string[];
  numQuizzesLabel: string = 'Total Available';
  numQuizzesArray!: number[];
  numUniqueAnsweredQuizzesLabel: string = 'Solved (Unique)';
  numUniqueAnsweredQuizzesArray!: number[];
  numAverageQuizzesLabel: string = 'Solved (Unique, Average per Student)';
  numAverageQuizzesArray!: number[];
  colors: string[] = ['#c0392b', '#2980b9', '#1abc9c'];

  async created() {
    this.labelsArray = this.quizStats
      .map((quizStats: QuizStats) => quizStats.courseExecutionYear.toString())
      .reverse();

    if (this.labelsArray.length > 0) {
      this.labelsArray[this.labelsArray.length - 1] += ' (current)';
    }

    this.numQuizzesArray = this.quizStats
      .map((quizStats: QuizStats) => quizStats.numQuizzes)
      .reverse();

    this.numUniqueAnsweredQuizzesArray = this.quizStats
      .map((quizStats: QuizStats) => quizStats.numUniqueAnsweredQuizzes)
      .reverse();

    this.numAverageQuizzesArray = this.quizStats
      .map((quizStats: QuizStats) => quizStats.averageQuizzesSolved)
      .reverse();
  }
}
</script>
