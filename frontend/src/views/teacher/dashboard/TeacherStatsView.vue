<template>
  <div class="container">
    <h2>Statistics for this course execution</h2>
    <div v-if="teacherDashboard != null" class="stats-container">
      <div class="items">
        <div ref="totalStudents" class="icon-wrapper">
          <animated-number :number="teacherDashboard.numStudents" data-cy="numStudents"/>
        </div>
        <div class="project-name">
          <p>Number of Quizzes</p>
        </div>
      </div>
      <div class="items">
        <div ref="uniqueQuizzesSolved" class="icon-wrapper">
          <animated-number
            :number="teacherDashboard.quizStats[0].uniqueQuizzesSolved"
          />
        </div>
        <div class="project-name">
          <p>Number of Quizzes Solved (Unique)</p>
        </div>
      </div>
      <div class="items">
        <div ref="averageQuizzesSolved" class="icon-wrapper">
          <animated-number
            :number="teacherDashboard.quizStats[0].averageQuizzesSolved"
          />
        </div>
        <div class="project-name">
          <p>Number of Quizzes Solved (Unique, Average Per Student)</p>
        </div>
      </div>
      <!-- QUESTIONS -->
      <!-------------------->
      <div class="items">
        <div ref="numAvailable" class="icon-wrapper">
          <animated-number :number="teacherDashboard.questionStats[0].numAvailable" data-cy="numAvailable"/>
        </div>
        <div class="project-name">
          <p>Number of Questions</p>
        </div>
      </div>
      <!-------------------->
      <div class="items">
        <div ref="answeredQuestionsUnique" class="icon-wrapper">
          <animated-number :number="teacherDashboard.questionStats[0].answeredQuestionsUnique" data-cy="answeredQuestionsUnique"/>
        </div>
        <div class="project-name">
          <p>Number of Questions Solves (Unique)</p>
        </div>
      </div>
      <!-------------------->
      <div class="items">
        <div ref="averageQuestionsAnswered" class="icon-wrapper">
          <animated-number :number="teacherDashboard.questionStats[0].averageQuestionsAnswered" data-cy="averageQuestionsAnswered" />
        </div>
        <div class="project-name">
          <p>Average Answered Questions</p>
        </div>
      </div>
      <div class="items">
        <div ref="numQuizzes" class="icon-wrapper">
          <animated-number :number="teacherDashboard.quizStats[0].numQuizzes" />
        </div>
        <div class="project-name">
          <p>Number of quizzes</p>
        </div>
      </div>
    </div>
    <h2 style="margin-bottom: 10px">
      Comparison with previous course executions
    </h2>
    <div v-if="teacherDashboard != null" class="stats-container" style="gap: 30px;">
      <div style="flex-direction: row">
        <h4 style="color: white; background-color: #2c3e50">Quizzes</h4>
        <div class="bar-chart">
          <div ref="quizStatsBarChart">
            <teacher-graphs-view
              :quizStats="teacherDashboard.quizStats"
            ></teacher-graphs-view>
          </div>
        </div>
      </div>
      <div style="flex-direction: row">
        <h4 style="color: white; background-color: #2c3e50">Questions</h4>
        <div class="bar-chart">
          <div ref="questionStatsBarChart">
            <question-chart
              :questionStats="teacherDashboard.questionStats"
            ></question-chart>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import AnimatedNumber from '@/components/AnimatedNumber.vue';
import TeacherDashboard from '@/models/dashboard/TeacherDashboard';
import TeacherGraphsView from '@/views/teacher/dashboard/TeacherGraphsView.vue';
import QuestionChart from '@/views/teacher/dashboard/QuestionChart.vue';

@Component({
  components: { AnimatedNumber, TeacherGraphsView, QuestionChart },
})
export default class TeacherStatsView extends Vue {
  @Prop() readonly dashboardId!: number;
  teacherDashboard: TeacherDashboard | null = null;

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.teacherDashboard = await RemoteServices.getTeacherDashboard();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>
<style lang="scss" scoped>
.stats-container-graph{
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
}
.stats-container {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
  align-items: stretch;
  align-content: center;
  height: 100%;
}

.items {
  background-color: rgba(255, 255, 255, 0.75);
  color: #1976d2;
  border-radius: 5px;
  flex-basis: 25%;
  margin: 20px;
  cursor: pointer;
  transition: all 0.6s;
}

.bar-chart {
  background-color: rgba(255, 255, 255, 0.9);
  height: 400px;
}

.icon-wrapper,
.project-name {
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-wrapper {
  font-size: 100px;
  transform: translateY(0px);
  transition: all 0.6s;
}

.icon-wrapper {
  align-self: end;
}

.project-name {
  align-self: start;
}

.project-name p {
  font-size: 24px;
  font-weight: bold;
  letter-spacing: 2px;
  transform: translateY(0px);
  transition: all 0.5s;
}

.items:hover {
  border: 3px solid black;

  & .project-name p {
    transform: translateY(-10px);
  }

  & .icon-wrapper i {
    transform: translateY(5px);
  }
}
</style>
