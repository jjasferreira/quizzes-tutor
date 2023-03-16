package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuestionStats;

public class QuestionStatsDto {
    
    private Integer id;

    private int numAvailable;

    private int answeredQuestionsUnique;

    private float averageQuestionsAnswered;

    public QuestionStatsDto() {

    }

    public QuestionStatsDto(QuestionStats questionStats) {
        this.id = questionStats.getId();
        this.numAvailable = questionStats.getNumAvailable();
        this.answeredQuestionsUnique = questionStats.getAnsweredQuestionsUnique();
        this.averageQuestionsAnswered = questionStats.getAverageQuestionsAnswered();
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getNumAvailable() {
        return this.numAvailable;
    }

    public void setNumAvailable(int numAvailable) {
        this.numAvailable = numAvailable;
    }

    public int getAnsweredQuestionsUnique() {
        return this.answeredQuestionsUnique;
    }

    public void setAnsweredQuestionsUnique(int answeredQuestionsUnique) {
        this.answeredQuestionsUnique = answeredQuestionsUnique;
    }

    public float getAverageQuestionsAnswered() {
        return this.averageQuestionsAnswered;
    }

    public void setAverageQuestionsAnswered(float averageQuestionsAnswered) {
        this.averageQuestionsAnswered = averageQuestionsAnswered;
    }

    @Override
    public String toString() {
        return "QuestionStatsDto{" +
                "id=" + this.id +
                ", numAvailable=" + this.numAvailable +
                ", answeredQuestionsUnique=" + this.answeredQuestionsUnique +
                ", averageQuestionsAnswered=" + this.averageQuestionsAnswered +
                '}';

    }
}

