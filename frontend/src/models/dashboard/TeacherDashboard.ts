import QuizStats from './QuizStats';
import QuestionStats from './QuestionStats'
import { Dictionary } from "vue-router/types/router";

export default class TeacherDashboard {
  id!: number;
  quizStats!: QuizStats[];
  questionStats!: QuestionStats[];
  // exclamation mark tells typescript that these values can never be NULL
  numStudents!: number;

  // questions
  numAvailable!: number;
  answeredQuestionsUnique!: number;
  averageQuestionsAnswered!: number;

  studentStats!: Array<Dictionary<number>>;


  constructor(jsonObj?: TeacherDashboard) {
    if (jsonObj) {
      this.id = jsonObj.id;
      if (jsonObj.quizStats) {
        this.quizStats = jsonObj.quizStats.map(
          (quizStats: QuizStats) => new QuizStats(quizStats)
        );
      }
      if (jsonObj.questionStats) {
        this.questionStats = jsonObj.questionStats.map(
          (questionStats : QuestionStats) => new QuestionStats(questionStats)
        );
      }
      
    }
  }
}
