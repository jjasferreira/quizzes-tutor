package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

public class QuestionStats {

    private int numAvailable;

    private int answeredQuestionUnique;

    private float averageQuestionsAnswered;


    public int getNumAvailable() {
        return numAvailable;
    }

    public int getAnsweredQuestionUnique() {
        return answeredQuestionUnique;
    }

    public float getAverageQuestionsAnswered() {
        return averageQuestionsAnswered;
    }


    public void update() {
        //TO DO
    }

}
