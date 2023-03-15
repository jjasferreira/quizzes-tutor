package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto.QuizStatsDto;

import java.util.ArrayList;
import java.util.List;

public class TeacherDashboardDto {
    private Integer id;
    private Integer numberOfStudents;
    private List<QuizStatsDto> quizStats;

    public TeacherDashboardDto() {
        this.quizStats = new ArrayList<>();
    }

    public TeacherDashboardDto(TeacherDashboard teacherDashboard) {
        this.id = teacherDashboard.getId();
        // For the number of students, we consider only active students
        this.numberOfStudents = teacherDashboard.getCourseExecution().getNumberOfActiveStudents();
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
                ", quizStats=" + this.getQuizStats() +
                "}";
    }
}
