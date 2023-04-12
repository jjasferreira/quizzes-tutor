import QuizStats from './QuizStats';
export default class TeacherDashboard {
  id!: number;
  quizStats!: QuizStats[];
  // exclamation mark tells typescript that these values can never be NULL
  numberOfStudents!: number;

  // questions
  numAvailable!: number;
  answeredQuestionsUnique!: number;
  averageQuestionsAnswered!: number;

  constructor(jsonObj?: TeacherDashboard) {
    if (jsonObj) {
      this.id = jsonObj.id;
      if (jsonObj.quizStats) {
        this.quizStats = jsonObj.quizStats.map(
          (quizStats: QuizStats) => new QuizStats(quizStats)
        );
      }
      this.numberOfStudents = jsonObj.numberOfStudents;

      // questions
      this.numAvailable = jsonObj.numAvailable;
      this.answeredQuestionsUnique = jsonObj.answeredQuestionsUnique;
      this.averageQuestionsAnswered = jsonObj.averageQuestionsAnswered;
    }
  }
}
