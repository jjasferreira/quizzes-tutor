package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;

import java.util.HashSet;
import java.util.Set;


import javax.persistence.*;


@Entity
public class QuizStats implements DomainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    private CourseExecution courseExecution;

    @ManyToOne
    private TeacherDashboard teacherDashboard;

    private int numQuizzes;

    private int uniqueQuizzesSolved;

    private float averageQuizzesSolved;

    public QuizStats() {
    }

    public QuizStats(CourseExecution courseExecution, TeacherDashboard teacherDashboard) {
        setCourseExecution(courseExecution);
        setTeacherDashboard(teacherDashboard);
    }

    public void remove() {
        teacherDashboard.getQuizStats().remove(this);
        courseExecution = null;
        teacherDashboard = null;
    }

    public Integer getId() {
        return id;
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
        this.teacherDashboard.addQuizStats(this);
    }

    public int getNumQuizzes() {
        return numQuizzes;
    }

    public void setNumQuizzes(int numQuizzes) {
        this.numQuizzes = numQuizzes;
    }

    public int getUniqueQuizzesSolved() {
        return uniqueQuizzesSolved;
    }

    public void setUniqueQuizzesSolved(int uniqueQuizzesSolved) {
        this.uniqueQuizzesSolved = uniqueQuizzesSolved;
    }

    public float getAverageQuizzesSolved() {
        return averageQuizzesSolved;
    }

    public void setAverageQuizzesSolved(float averageQuizzesSolved) {
        this.averageQuizzesSolved = averageQuizzesSolved;
    }

    private int getNumQuizzesExecution() {
        int quizCount = 0;
        for (Quiz quiz: getCourseExecution().getQuizzes()) {
            quizCount++;
        }
        return quizCount;
    }

    private int getUniqueQuizzesSolvedExecution() {
        int quizCount = 0;
        for (Quiz quiz: getCourseExecution().getQuizzes()) {
            for (QuizAnswer quizAnswer: quiz.getQuizAnswers()) {
                if (quizAnswer.isCompleted()) {
                    quizCount++;
                    break;
                }
            }
        }
        return quizCount;
    }

    private float getAverageQuizzesSolvedExecution() {
        int quizCount = 0;
        int studentCount = 0;
        CourseExecution execution = getCourseExecution();
        for (Student student: execution.getStudents()) {
            studentCount++;
            for (QuizAnswer quizAnswer: student.getQuizAnswers()) {
                if (quizAnswer.getQuiz().getCourseExecution() == execution && quizAnswer.isCompleted()) {
                    quizCount++;
                }
            }
        }
        if (studentCount == 0) {
            return 0;
        }
        return (float) quizCount / studentCount;
    }

    @Override
    public String toString() {
        return "QuizStats{id=" + id +
                ", courseExecution=" + courseExecution +
                ", teacherDashboard=" + teacherDashboard +
                ", numQuizzes=" + numQuizzes +
                ", uniqueQuizzesSolved=" + uniqueQuizzesSolved +
                ", averageQuizzesSolved=" + averageQuizzesSolved +
                "}";
    }

    @Override
    public void accept(Visitor visitor) {
        // Only used for XML generation
    }

    public void update(){
        setNumQuizzes(getNumQuizzesExecution());
        setUniqueQuizzesSolved(getUniqueQuizzesSolvedExecution());
        setAverageQuizzesSolved(getAverageQuizzesSolvedExecution());
    }

}