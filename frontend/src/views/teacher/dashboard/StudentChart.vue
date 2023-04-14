<template>
  <div v-if="studentStats.length > 0">
    <bar-chart
      :labels="labelsArray"
      :label1="numStudentsLabel"
      :data1="numStudentsArray"
      :label2="numMore75CorrectQuestionsLabel"
      :data2="numMore75CorrectQuestionsArray"
      :label3="numAtLeast3QuizzesLabel"
      :data3="numAtLeast3QuizzesArray"
      :colors="colors"
    ></bar-chart>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import BarChart from '@/components/BarChart.vue';
import TeacherDashboardStudentStats from '@/models/dashboard/TeacherDashboardStudentStats';

@Component({
  components: { BarChart },
})
export default class StudentChart extends Vue {
  @Prop() readonly studentStats!: TeacherDashboardStudentStats[];

  labelsArray!: string[];
  numStudentsLabel: string = 'Total Students';
  numStudentsArray!: number[];
  numMore75CorrectQuestionsLabel: string = 'Solved (75% or more correct)';
  numMore75CorrectQuestionsArray!: number[];
  numAtLeast3QuizzesLabel: string = 'Solved (at least 3 quizzes)';
  numAtLeast3QuizzesArray!: number[];
  colors: string[] = ['#c0392b', '#2980b9', '#1abc9c'];

  async created() {
    this.labelsArray = this.studentStats
      .map((studentStats: TeacherDashboardStudentStats) =>
        studentStats.courseExecutionYear.toString()
      )
      .reverse();

    if (this.labelsArray.length > 0) {
      this.labelsArray[this.labelsArray.length - 1] += ' (current)';
    }

    this.numStudentsArray = this.studentStats
      .map(
        (studentStats: TeacherDashboardStudentStats) => studentStats.numStudents
      )
      .reverse();

    this.numMore75CorrectQuestionsArray = this.studentStats
      .map(
        (studentStats: TeacherDashboardStudentStats) =>
          studentStats.numMore75CorrectQuestions
      )
      .reverse();

    this.numAtLeast3QuizzesArray = this.studentStats
      .map(
        (studentStats: TeacherDashboardStudentStats) =>
          studentStats.numAtLeast3Quizzes
      )
      .reverse();
  }
}
</script>
