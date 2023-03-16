package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard;

public class TeacherDashboardDto {
    private Integer id;
    private Integer numberOfStudents;
    private List<QuestionStatsDto> questionStats;

    public TeacherDashboardDto() {
        this.questionStats = new ArrayList<>();
    }

    public TeacherDashboardDto(TeacherDashboard teacherDashboard) {
        this.id = teacherDashboard.getId();
        // For the number of students, we consider only active students
        this.numberOfStudents = teacherDashboard.getCourseExecution().getNumberOfActiveStudents();
        this.questionStats = new ArrayList<>();
        teacherDashboard.getQuestion().forEach(questionStats -> this.questionStats.add(new QuestionStatsDto(questionStats)));
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

    public List<QuestionStatsDto> getQuestionStats() {
        return this.questionStats;
    }

    public void addQuestionStats(QuestionStatsDto questionStats) {
        this.questionStats.add(questionStats);
    }

    @Override
    public String toString() {
        return "TeacherDashboardDto{" +
                "id=" + id +
                ", numberOfStudents=" + this.getNumberOfStudents() +
                ", questionStats=" + this.getQuestionStats() +
                "}";
    }
}
