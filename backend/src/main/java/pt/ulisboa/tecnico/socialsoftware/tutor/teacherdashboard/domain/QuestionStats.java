package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;

@Entity
public class QuestionStats implements DomainEntity {

    public QuestionStats() {}

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
        this.answeredQuestionUnique = getNumUniqueQuestionsAnswered();
        this.averageQuestionsAnswered = getAverageAnsweredQuestions();
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

    private int getNumUniqueQuestionsAnswered() {
        Set<Question> uniqueQuestionsAnswered = new HashSet<>();

        for(Quiz quiz : this.execution.getQuizzes()) {
            for (QuizAnswer quizAnswer : quiz.getQuizAnswers()) {
                if (quizAnswer.getStudent() != null) {
                    for (QuestionAnswer questionAnswer : quizAnswer.getQuestionAnswers())
                        if (!uniqueQuestionsAnswered.contains(questionAnswer.getQuestion())) {
                            uniqueQuestionsAnswered.add(questionAnswer.getQuestion());
                        }
                    }
                }
            }

        return uniqueQuestionsAnswered.size();
    }

    private float getAverageAnsweredQuestions() {
        int sum = 0;

        // por cada student da execution
        for(Student student : this.execution.getStudents()){
            // por cada quiz da execution
            for(Quiz quiz : this.execution.getQuizzes()){
                // verifica se o student fez o quiz
                if(student.getQuizAnswer(quiz) != null){
                    // vai buscar cada resposta do student a esse quiz
                    for(QuestionAnswer questionAnswer : student.getQuizAnswer(quiz).getQuestionAnswers()){
                        // vai buscar a pergunta que lhe foi feita, e ve se ja nao foi feita anteriormente
                        // acrescenta uma pergunta nao repetida
                        sum++;
                    }
                }
            }
            // acrescenta numero de perguntas unicas de cada estudante
        }
        if(this.execution.getStudents().size() == 0) return 0;
        return (float)sum/this.execution.getStudents().size();
    }

    public void accept(Visitor visitor) {
        // Only to generate XML
    }

}
