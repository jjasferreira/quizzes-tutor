package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import javax.persistence.*;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;

@Entity
public class QuestionStats implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int numAvailable;

    private int answeredQuestionUnique;

    private float averageQuestionsAnswered;

    @OneToOne
    private CourseExecution execution;

    @ManyToOne
    private TeacherDashboard teacherDashboard;

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

    private int getNumAvailableQuestions() {
        int questionCounter = 0;
        for(Question question : this.execution.getCourse().getQuestions()){
            if(question.getStatus() == Question.Status.AVAILABLE){
                questionCounter++;
            }
        }
        return questionCounter;
    }

    public void accept(Visitor visitor) {
        // Only to generate XML
    }

}
