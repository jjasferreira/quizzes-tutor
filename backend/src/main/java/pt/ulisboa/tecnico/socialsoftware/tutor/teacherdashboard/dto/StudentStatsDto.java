package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.StudentStats;

public class StudentStatsDto {
    private Integer id;
    private Integer numStudents;
    private Integer numMore75CorrectQuestions;
    private Integer numAtLeast3Quizzes;

    public StudentStatsDto(StudentStats studentStats) {
        this.id = studentStats.getId();
        this.numStudents = studentStats.getNumStudents();
        this.numMore75CorrectQuestions = studentStats.getNumMore75CorrectQuestions();
        this.numAtLeast3Quizzes = studentStats.getNumAtLeast3Quizzes();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumStudents() {
        return numStudents;
    }

    public void setNumStudents(Integer numStudents) {
        this.numStudents = numStudents;
    }

    public Integer getNumMore75CorrectQuestions() {
        return numMore75CorrectQuestions;
    }

    public void setNumMore75CorrectQuestions(Integer numMore75CorrectQuestions) {
        this.numMore75CorrectQuestions = numMore75CorrectQuestions;
    }

    public Integer getNumAtLeast3Quizzes() {
        return numAtLeast3Quizzes;
    }

    public void setNumAtLeast3Quizzes(Integer numAtLeast3Quizzes) {
        this.numAtLeast3Quizzes = numAtLeast3Quizzes;
    }

    @Override
    public String toString() {
        return "StudentStatsDto{"
                + "id="
                + id
                + ", numStudents="
                + numStudents
                + ", numMore75CorrectQuestions="
                + numMore75CorrectQuestions
                + ", numAtLeast3Quizzes="
                + numAtLeast3Quizzes
                + '}';
    }
}
