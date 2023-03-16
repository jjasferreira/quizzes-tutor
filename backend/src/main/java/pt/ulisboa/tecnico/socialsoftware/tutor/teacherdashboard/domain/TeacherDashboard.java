package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;


@Entity
public class TeacherDashboard implements DomainEntity {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dashboard", orphanRemoval = true)
    private List<QuestionStats> question = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private CourseExecution courseExecution;

    @ManyToOne
    private Teacher teacher;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "teacherDashboard", orphanRemoval = true)
    private final Set<StudentStats> studentStats = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "teacherDashboard", orphanRemoval = true)
    private List<QuizStats> quizStats = new ArrayList<>();

    public TeacherDashboard() {
    }

    public TeacherDashboard(CourseExecution courseExecution, Teacher teacher) {
        setCourseExecution(courseExecution);
        setTeacher(teacher);
    }

    public void remove() {
        teacher.getDashboards().remove(this);
        teacher = null;
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

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
        this.teacher.addDashboard(this);
    }

    public Set<StudentStats> getStudentStats() {
        return studentStats;
    }

    public List<QuestionStats> getQuestionStats(){
        return this.question;
    }

    public void addStudentStats(StudentStats studentStats) {
        this.studentStats.add(studentStats);
    }

    public void accept(Visitor visitor) {
        // Only used for XML generation
    }
    public void addQuestionStats(QuestionStats questionStat) {
        this.question.add(questionStat);
    }

    public List<QuestionStats> getQuestion() {return this.question;}

    @Override
    public String toString() {
        return "Dashboard{" +
                "id=" + id +
                ", courseExecution=" + courseExecution +
                ", teacher=" + teacher +
                '}';
    }

    public List<QuizStats> getQuizStats() {
        return quizStats;
    }
    public void addQuizStats(QuizStats quizStats) {
        if (!this.quizStats.contains(quizStats))
            this.quizStats.add(quizStats);
    }
    public void update() {
        this.quizStats.forEach(QuizStats::update);
        this.studentStats.forEach(StudentStats::update);
        this.question.forEach(QuestionStats::update);
    }

}
