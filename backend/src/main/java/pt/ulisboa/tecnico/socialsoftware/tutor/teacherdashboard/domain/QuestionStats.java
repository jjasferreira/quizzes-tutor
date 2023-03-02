package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission;
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
        this.answeredQuestionUnique = getNumUniqueAnswers();
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

    private int getNumUniqueAnswers() {
        Set<QuestionSubmission> uniqueSubmissions = new HashSet<>();

        for(QuestionSubmission submission : this.execution.getQuestionSubmissions()) {
            if(!uniqueSubmissions.contains(submission)) {
                uniqueSubmissions.add(submission);
            }
        }

        return uniqueSubmissions.size();
    }

    public void accept(Visitor visitor) {
        // Only to generate XML
    }

}
