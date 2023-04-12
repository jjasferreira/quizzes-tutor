import StudentStats from './StudentStats';
import QuizStats from './QuizStats';
import QuestionStats from './QuestionStats'

export default class TeacherDashboard {
  id!: number;
  studentStats!: StudentStats[];
  quizStats!: QuizStats[];
  questionStats!: QuestionStats[];

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
    if (jsonObj?.studentStats) {
      this.studentStats = jsonObj.studentStats.map(
        (studentStats: StudentStats) => new StudentStats(studentStats)
      );
    }
  }
}
