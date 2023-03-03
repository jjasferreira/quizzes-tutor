package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.FailedAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.StudentDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question



// Número médio de perguntas únicas respondidas por aluno

@DataJpaTest
class GetAverageQuestionsAnsweredTest extends SpockTest {
    def student
    def student2
    def question
    def question2
    def question3

    def quiz
    def quizQuestion
    def quizAnswer
    def dashboard

    def setup() {
        createExternalCourseAndExecution()



        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle(QUIZ_TITLE)
        quiz.setCourseExecution(externalCourseExecution)


        question = new Question()
        question.setKey(1)
        question.setStatus(Question.Status.AVAILABLE)
        question.setCourse(externalCourse)
        question.setTitle("Question title")

        question2 = new Question()
        question2.setKey(1)
        question2.setStatus(Question.Status.AVAILABLE)
        question2.setCourse(externalCourse)
        question2.setTitle("Question title")

        question3 = new Question()
        question3.setStatus(Question.Status.AVAILABLE)
        question3.setKey(1)
        question3.setCourse(externalCourse)
        question3.setTitle("Question title")

        externalCourse.addQuestion(question)
        externalCourse.addQuestion(question2)
        externalCourse.addQuestion(question3)

    }

    def "noStudents"() {
        given:
            quizQuestion = new QuizQuestion(quiz, question, 1)

            quizAnswer = new QuizAnswer()
            quizAnswer.setQuiz(quiz)
            QuestionAnswer questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, 1)
            quizAnswer.addQuestionAnswer(questionAnswer)

            quiz.addQuizQuestion(quizQuestion)

            QuestionStats stats = new QuestionStats(externalCourseExecution)

        when:
            stats.update()

        then:
            stats.getAverageQuestionsAnswered() == (float)0

    }
    def "getUniqueAnswersDuplicate"() {
        given:

            student = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
            student.addCourse(externalCourseExecution)
            userRepository.save(student)
            dashboard = new StudentDashboard(externalCourseExecution, student)

            student2 = new Student(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, false, AuthUser.Type.TECNICO)
            student2.addCourse(externalCourseExecution)
            userRepository.save(student2)

            QuestionStats stats = new QuestionStats(externalCourseExecution)


            Quiz newQuiz = createQuiz(Quiz.QuizType.EXAM.toString())
            Question newQuestion = createQuestion()
            Question newQuestion2 = createQuestion()

            QuizQuestion quizQuestion1 = new QuizQuestion(newQuiz,newQuestion,1)
            quizQuestionRepository.save(quizQuestion1)


            def questionAnswer = answerQuiz(true, false, true, quizQuestion1, newQuiz)

            Quiz newQuiz_ = createQuiz(Quiz.QuizType.EXAM.toString())
            Question newQuestion_ = createQuestion()
            Question newQuestion2_ = createQuestion()
            QuizQuestion quest = createQuizQuestion(newQuiz_, newQuestion_)
            quizQuestionRepository.save(quest)


            def questionAnswer_ = answerQuiz(true, false, true, quest, newQuiz_)

        when:
            stats.update()

        then:
            stats.getAverageQuestionsAnswered() == (float)1

    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {  }

    def createQuestion() {
        def newQuestion = new Question()
        newQuestion.setTitle("Question Title")
        newQuestion.setCourse(externalCourse)
        def questionDetails = new MultipleChoiceQuestion()
        newQuestion.setQuestionDetails(questionDetails)
        questionRepository.save(newQuestion)

        def option = new Option()
        option.setContent("Option Content")
        option.setCorrect(true)
        option.setSequence(0)
        option.setQuestionDetails(questionDetails)
        optionRepository.save(option)
        def optionKO = new Option()
        optionKO.setContent("Option Content")
        optionKO.setCorrect(false)
        optionKO.setSequence(1)
        optionKO.setQuestionDetails(questionDetails)
        optionRepository.save(optionKO)

        return newQuestion;
    }

    def createQuiz(type = Quiz.QuizType.PROPOSED.toString()) {
        def quiz = new Quiz()
        quiz.setTitle("Quiz Title")
        quiz.setType(type)
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setCreationDate(DateHandler.now())
        quiz.setAvailableDate(DateHandler.now())
        quizRepository.save(quiz)
        return quiz
    }

    def createQuizQuestion(quiz, question) {
        def quizQuestion = new QuizQuestion(quiz, question, 0)
        quizQuestionRepository.save(quizQuestion)
        return quizQuestion
    }

    def answerQuiz(answered, correct, completed, quizQuestion, quiz, date = DateHandler.now()) {
        def quizAnswer = new QuizAnswer()
        quizAnswer.setCompleted(completed)
        quizAnswer.setCreationDate(date)
        quizAnswer.setAnswerDate(date)
        quizAnswer.setStudent(student)
        quizAnswer.setQuiz(quiz)
        quizAnswerRepository.save(quizAnswer)

        def questionAnswer = new QuestionAnswer()
        questionAnswer.setTimeTaken(1)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswerRepository.save(questionAnswer)

        def answerDetails
        def correctOption = quizQuestion.getQuestion().getQuestionDetails().getCorrectOption()
        def incorrectOption = quizQuestion.getQuestion().getQuestionDetails().getOptions().stream().filter(option -> option != correctOption).findAny().orElse(null)
        if (answered && correct) answerDetails = new MultipleChoiceAnswer(questionAnswer, correctOption)
        else if (answered && !correct) answerDetails = new MultipleChoiceAnswer(questionAnswer, incorrectOption)
        else {
            questionAnswerRepository.save(questionAnswer)
            return questionAnswer
        }
        questionAnswer.setAnswerDetails(answerDetails)
        answerDetailsRepository.save(answerDetails)
        return questionAnswer
    }



    def createFailedAnswer(questionAnswer, collected) {
        def failedAnswer = new FailedAnswer(dashboard, questionAnswer, collected)
        failedAnswerRepository.save(failedAnswer)
        return failedAnswer
    }
}