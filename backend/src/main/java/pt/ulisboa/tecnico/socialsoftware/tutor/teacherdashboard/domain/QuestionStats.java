package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;


public class QuestionStats {

    private int numAvailable;

    private int answeredQuestionUnique;

    private float averageQuestionsAnswered;

    private CourseExecution execution;

    public QuestionStats(CourseExecution courseExecution){
        this.execution = courseExecution;
    }

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
        this.numAvailable = getNumAvailableQuestions();
    }

    private int getNumAvailableQuestions(){
        int questionCounter = 0;
        for(Question question : this.execution.getCourse().getQuestions()){
            if(question.getStatus() == Question.Status.AVAILABLE){
                questionCounter++;
            }
        }
        return questionCounter;
    }

}
