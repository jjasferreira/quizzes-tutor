import QuizStats from './QuizStats';
export default class TeacherDashboard {
  id!: number;
  quizStats!: QuizStats[];

  constructor(jsonObj?: TeacherDashboard) {
    if (jsonObj) {
      this.id = jsonObj.id;
      if (jsonObj.quizStats) {
        this.quizStats = jsonObj.quizStats.map(
          (quizStats: QuizStats) => new QuizStats(quizStats)
        );
      }
    }
  }
}
