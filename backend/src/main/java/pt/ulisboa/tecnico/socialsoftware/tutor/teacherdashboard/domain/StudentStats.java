package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;

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

    public StudentStats() {
    }

    public StudentStats(CourseExecution courseExecution) {
        setCourseExecution(courseExecution);
    }

    public Integer getId() {
        return id;
    }

    public int getNumStudents() {return numStudents;}

    public void setNumStudents(int numStudents) {this.numStudents = numStudents;
    }

    public int getNumMore75CorrectQuestions() {return numMore75CorrectQuestions;
    }

    public void setNumMore75CorrectQuestions(int numMore75CorrectQuestions) {this.numMore75CorrectQuestions = numMore75CorrectQuestions;
    }

    public int getNumAtLeast3Quizzes() {return numAtLeast3Quizzes;
    }

    public void setNumAtLeast3Quizzes(int numAtLeast3Quizzes) {this.numAtLeast3Quizzes = numAtLeast3Quizzes;
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
    }

    public int getTotalStudentNumber(){
        int studentCount = 0;
        for (User user: getCourseExecution().getUsers()){
            if (user.getRole() == User.Role.STUDENT){
                studentCount++;
            }
        }
        return studentCount;
    }

    public void update(){
        setNumStudents(getTotalStudentNumber());
    }

    @Override
    public String toString() {
        return "StudentStats{" +
                "id=" + id +
                ", courseExecution=" + courseExecution +
                ", teacherDashboard=" + teacherDashboard +
                ", numStudents=" + numStudents +
                ", numMore75CorrectQuestions=" + numMore75CorrectQuestions +
                ", getNumAtLeast3Quizzes=" + numAtLeast3Quizzes +
                '}';
    }

    @Override
    public void accept(Visitor visitor) {
        // Only used for XML generation
    }
}
