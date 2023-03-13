package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;
import javax.persistence.*;

@Entity
public class StudentStats implements DomainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    private CourseExecution courseExecution;

    @ManyToOne
    private TeacherDashboard teacherDashboard;

    private int numStudents;

    private int numMore75CorrectQuestions;

    private int numAtLeast3Quizzes;

    public StudentStats() {}

    public StudentStats(CourseExecution courseExecution, TeacherDashboard teacherDashboard) {
        setCourseExecution(courseExecution);
        setTeacherDashboard(teacherDashboard);
    }

    public Integer getId() {
        return id;
    }

    public int getNumStudents() {
        return numStudents;
    }

    public void setNumStudents(int numStudents) {
        this.numStudents = numStudents;
    }

    public int getNumMore75CorrectQuestions() {
        return numMore75CorrectQuestions;
    }

    public void setNumMore75CorrectQuestions(int numMore75CorrectQuestions) {
        this.numMore75CorrectQuestions = numMore75CorrectQuestions;
    }

    public int getNumAtLeast3Quizzes() {
        return numAtLeast3Quizzes;
    }

    public void setNumAtLeast3Quizzes(int numAtLeast3Quizzes) {
        this.numAtLeast3Quizzes = numAtLeast3Quizzes;
    }

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }

    public TeacherDashboard getTeacherDashboard() {
        return teacherDashboard;
    }

    public void setTeacherDashboard(TeacherDashboard teacherDashboard) {
        this.teacherDashboard = teacherDashboard;
        teacherDashboard.addStudentStats(this);
    }

    private int calculateTotalStudentNumber() {
        return this.courseExecution.getStudents().size();
    }

    private int calculateStudentsOver75Correct() {
        int studentsCount = 0;
        long totalQuestions = 0;
        long correctAnswers = 0;
        for (Student student : this.courseExecution.getStudents()) {
            for (QuizAnswer answer : student.getQuizAnswers()) {
                if (answer.isCompleted()
                        && answer.getQuiz().getCourseExecution().equals(this.courseExecution)) {
                    totalQuestions += answer.getQuiz().getQuizQuestionsNumber();
                    correctAnswers += answer.getNumberOfCorrectAnswers();
                }
            }

            if (correctAnswers > 0.75 * totalQuestions) {
                studentsCount++;
            }
        }

        return studentsCount;
    }

    private int calculateNumAtLeast3Quizzes() {
        int studentsCount = 0;
        for (Student student : this.courseExecution.getStudents()) {
            int completedQuizzes = 0;
            for (QuizAnswer answer : student.getQuizAnswers()) {
                if (answer.isCompleted()
                        && answer.getQuiz().getCourseExecution().equals(this.courseExecution)) {
                    completedQuizzes++;
                }

                if (completedQuizzes >= 3) {
                    studentsCount++;
                    break;
                }
            }
        }
        return studentsCount;
    }

    public void update() {
        setNumStudents(calculateTotalStudentNumber());
        setNumMore75CorrectQuestions(calculateStudentsOver75Correct());
        setNumAtLeast3Quizzes(calculateNumAtLeast3Quizzes());
    }

    @Override
    public String toString() {
        return "StudentStats{"
                + "id="
                + id
                + ", courseExecution="
                + courseExecution
                + ", teacherDashboard="
                + teacherDashboard
                + ", numStudents="
                + numStudents
                + ", numMore75CorrectQuestions="
                + numMore75CorrectQuestions
                + ", getNumAtLeast3Quizzes="
                + numAtLeast3Quizzes
                + '}';
    }

    @Override
    public void accept(Visitor visitor) {
        // Only used for XML generation
    }
}
