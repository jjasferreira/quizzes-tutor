<template>
  <div class="container">
    <h2>Statistics for this course execution</h2>
    <div v-if="teacherDashboard != null" class="stats-container">
      <div class="items">
        <div ref="numQuizzes" class="icon-wrapper">
          <animated-number :number="teacherDashboard.quizStats[0].numQuizzes" />
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
          <animated-number :number="teacherDashboard.numAvailable" data-cy="numAvailable"/>
        </div>
        <div class="project-name">
          <p>Number of Questions</p>
        </div>
      </div>
      <!-------------------->
      <div class="items">
        <div ref="answeredQuestionsUnique" class="icon-wrapper">
          <animated-number :number="teacherDashboard.answeredQuestionsUnique" data-cy="answeredQuestionsUnique"/>
        </div>
        <div class="project-name">
          <p>Number of Questions Solves (Unique)</p>
        </div>
      </div>
      <!-------------------->
      <div class="items">
        <div ref="averageQuestionsAnswered" class="icon-wrapper">
          <animated-number :number="teacherDashboard.averageQuestionsAnswered" data-cy="averageQuestionsAnswered" />
        </div>
        <div class="project-name">
          <p>Number of Questions Correctly Solved</p>
        </div>
      </div>
    </div>
<<<<<<< HEAD
    <h2 style="margin-bottom: 10px">
      Comparison with previous course executions
    </h2>
    <div v-if="teacherDashboard != null" class="stats-container">
      <div style="flex-direction: column">
        <h4 style="color: white; background-color: #2c3e50">Quizzes</h4>
        <div class="bar-chart">
          <div ref="quizStatsBarChart">
            <teacher-graphs-view
              :quizStats="teacherDashboard.quizStats"
            ></teacher-graphs-view>
          </div>
        </div>
      </div>
    </div>
  </div>
=======
    <div  v-if="teacherDashboard != null" class="stats-container">
      <h2>Comparison with previous course executions</h2>

      <!-- Questions -->
      <!----<div class="items">
        <Bar  id="question-stats-chart" 
              :options="chartOptions" 
              :data="question-stats-chart" />
      </div>-->

    </div>

    
</div>
>>>>>>> 66976829 (fix: Bug in stats Refs: #92)
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import AnimatedNumber from '@/components/AnimatedNumber.vue';
import TeacherDashboard from '@/models/dashboard/TeacherDashboard';
<<<<<<< HEAD
import TeacherGraphsView from '@/views/teacher/dashboard/TeacherGraphsView.vue';
=======
// chart.js needs to be imported as legacy to work with vue2
import { Bar } from 'vue-chartjs/legacy'
import { Chart as Chart, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale } from 'chart.js'

>>>>>>> 66976829 (fix: Bug in stats Refs: #92)

@Component({
  components: { AnimatedNumber, TeacherGraphsView },
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
.stats-container {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
  align-items: stretch;
  align-content: center;
  height: 100%;

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
