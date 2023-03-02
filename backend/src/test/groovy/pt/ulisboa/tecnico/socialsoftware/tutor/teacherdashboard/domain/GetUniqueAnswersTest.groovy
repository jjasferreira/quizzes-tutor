package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.*

import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.StudentDashboard
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion


@DataJpaTest
class GetUniqueAnswersTest extends SpockTest{
    def student
    def student2
    def question
    def quiz
    def quizquestion
    def quizAnswer

    def setup(){
        createExternalCourseAndExecution()

        student = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        student.addCourse(externalCourseExecution)
        userRepository.save(student)

        student2 = new Student(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, false, AuthUser.Type.TECNICO)
        student2.addCourse(externalCourseExecution)
        userRepository.save(student2)

        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle(QUIZ_TITLE)
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setAvailableDate(LOCAL_DATE_BEFORE)
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setOneWay(true)

        question = new Question()
        question.setKey(1)
        question.setCourse(externalCourse)
        question.setTitle("Question title")
    }

    def "getUniqueAnswersNoDuplicate"(){
        given:
            quizquestion = new QuizQuestion(quiz, question, 1)

            quizAnswer = new QuizAnswer(student, quiz)
            QuestionAnswer questionAnswer = new QuestionAnswer(quizAnswer,quizquestion,1)
            quizAnswer.addQuestionAnswer(questionAnswer)

            quiz.addQuizQuestion(quizquestion)

            QuestionStats stats = new QuestionStats(externalCourseExecution)

        when:
            stats.update()

        then:
            stats.getAnsweredQuestionUnique() == 1
    }

    def "getUniqueAnswersDuplicate"(){
        given:

        quizquestion = new QuizQuestion(quiz, question, 1)

        quizAnswer = new QuizAnswer(student, quiz)
        QuestionAnswer questionAnswer = new QuestionAnswer(quizAnswer,quizquestion,1)
        quizAnswer.addQuestionAnswer(questionAnswer)

        quiz.addQuizQuestion(quizquestion)

        QuestionStats stats = new QuestionStats(externalCourseExecution)

        Question question2 = new Question()
        question2.setKey(2)
        question2.setCourse(externalCourse)
        question2.setTitle("Question title")

        Question question3 = new Question()
        question3.setKey(3)
        question3.setCourse(externalCourse)
        question3.setTitle("Question title")

        def quizquestion2 = new QuizQuestion(quiz, question2, 2)
        def quizquestion3 = new QuizQuestion(quiz, question3, 3)

        QuizAnswer quizAnswer2 = new QuizAnswer(student2, quiz)
        QuizAnswer quizAnswer1 = new QuizAnswer(student, quiz)

        QuestionAnswer questionAnswer2 = new QuestionAnswer(quizAnswer2,quizquestion,1)
        QuestionAnswer questionAnswer3 = new QuestionAnswer(quizAnswer2,quizquestion2,2)
        QuestionAnswer questionAnswer4 = new QuestionAnswer(quizAnswer2,quizquestion3,3)
        QuestionAnswer questionAnswer5 = new QuestionAnswer(quizAnswer1,quizquestion3,3)


        quizAnswer.addQuestionAnswer(questionAnswer2)
        quizAnswer.addQuestionAnswer(questionAnswer3)
        quizAnswer.addQuestionAnswer(questionAnswer4)
        quizAnswer.addQuestionAnswer(questionAnswer5)

        when:
        stats.update()

        then:
        stats.getAnsweredQuestionUnique() == 3

    }

    def "testInvalidAnswer"(){
        given:
        QuestionStats stats = new QuestionStats(externalCourseExecution)

        QuizAnswer quizAnswer1 = new QuizAnswer()
        quizAnswer1.setQuiz(quiz)


        quizquestion = new QuizQuestion(quiz, question, 1)
        QuestionAnswer questionAnswer = new QuestionAnswer(quizAnswer1,quizquestion,1)
        quizAnswer1.addQuestionAnswer(questionAnswer)

        quiz.addQuizQuestion(quizquestion)

        when:
        stats.update()

        then:
        stats.getAnsweredQuestionUnique() == 0

    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {  }
}
