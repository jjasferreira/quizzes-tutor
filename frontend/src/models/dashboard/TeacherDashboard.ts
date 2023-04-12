import QuizStats from './QuizStats';
import { Dictionary } from "vue-router/types/router";

export default class TeacherDashboard {
  id!: number;
  quizStats!: QuizStats[];
  // exclamation mark tells typescript that these values can never be NULL
  numStudents!: number;

  // questions
  numAvailable!: number;
  answeredQuestionsUnique!: number;
  averageQuestionsAnswered!: number;

  studentStats!: Array<Dictionary<number>>;
  questionStats!: Array<Dictionary<number>>;

  constructor(jsonObj?: TeacherDashboard) {
    if (jsonObj) {
      this.id = jsonObj.id;
      if (jsonObj.quizStats) {
        this.quizStats = jsonObj.quizStats.map(
          (quizStats: QuizStats) => new QuizStats(quizStats)
        );
      }
      
      if(jsonObj.questionStats){
        this.questionStats = jsonObj.questionStats;
        this.studentStats = jsonObj.studentStats;
        this.numStudents = this.studentStats[0]['numStudents'];
        
        this.numAvailable = this.questionStats[0]['numAvailable'];
        this.answeredQuestionsUnique = this.questionStats[0]['answeredQuestionsUnique'];
        this.averageQuestionsAnswered = this.questionStats[0]['averageQuestionsAnswered'];
      }
      console.log(this.questionStats[0])
    }
  }
}
