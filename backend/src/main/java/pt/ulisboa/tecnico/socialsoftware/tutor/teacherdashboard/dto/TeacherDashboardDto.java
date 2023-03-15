package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto;

import java.util.ArrayList;
import java.util.List;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto.QuizStatsDto;

import java.util.ArrayList;
import java.util.List;

public class TeacherDashboardDto {
    private Integer id;
    private Integer numberOfStudents;

    private List<QuestionStatsDto> questionStats;

    private ArrayList<StudentStatsDto> studentStatsDtos;
    private List<QuizStatsDto> quizStats;

    public TeacherDashboardDto() {
        this.questionStats = new ArrayList<>();
        this.quizStats = new ArrayList<>();
    }

    public TeacherDashboardDto(TeacherDashboard teacherDashboard) {
        this.id = teacherDashboard.getId();
        // For the number of students, we consider only active students
        this.numberOfStudents = teacherDashboard.getCourseExecution().getNumberOfActiveStudents();
        this.studentStatsDtos = new ArrayList<>();
        teacherDashboard
                .getStudentStats()
                .forEach(
                        studentStats -> this.addStudentStatsDto(new StudentStatsDto(studentStats)));
        this.questionStats = new ArrayList<>();
        teacherDashboard.getQuestion().forEach(questionStats -> this.questionStats.add(new QuestionStatsDto(questionStats)));
        // Add the DTOs of all quizStats to the list
        this.quizStats = new ArrayList<>();
        teacherDashboard.getQuizStats().forEach(quizStats -> this.quizStats.add(new QuizStatsDto(quizStats)));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumberOfStudents() {
        return numberOfStudents;
    }

    public void setNumberOfStudents(Integer numberOfStudents) {
        this.numberOfStudents = numberOfStudents;
    }

    public ArrayList<StudentStatsDto> getStudentStatsDtos() {
        return studentStatsDtos;
    }

    public void addStudentStatsDto(StudentStatsDto studentStatsDto) {
        this.studentStatsDtos.add(studentStatsDto);
    }

    public List<QuestionStatsDto> getQuestionStats() {
        return this.questionStats;
    }

    public void addQuestionStats(QuestionStatsDto questionStats) {
        this.questionStats.add(questionStats);
    public List<QuizStatsDto> getQuizStats() {
        return quizStats;
    }

    public void addQuizStats(QuizStatsDto quizStats) {
        this.quizStats.add(quizStats);
    }

    @Override
    public String toString() {
        return "TeacherDashboardDto{" +
                "id=" + id +
                ", numberOfStudents=" + this.getNumberOfStudents() +
                ", questionStats=" + this.getQuestionStats() +
                ", quizStats=" + this.getQuizStats() +
                "}";
    }
}
