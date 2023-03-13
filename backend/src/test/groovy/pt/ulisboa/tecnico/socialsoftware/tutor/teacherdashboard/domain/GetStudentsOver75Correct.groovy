package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher

@DataJpaTest
class GetStudentsOver75Correct extends SpockTest {
    public static final String USER_4_USERNAME = "rgm"
    public static final String USER_4_NAME = "User 4 Name"
    public static final String USER_4_EMAIL = "user4@mail.com"

    Teacher teacher;

    def setup() {
        createExternalCourseAndExecution()

        teacher = new Teacher(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        teacher.addCourse(externalCourseExecution)
        userRepository.save(teacher)

        // Create student A and B
        def studentA = new Student(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, false, AuthUser.Type.TECNICO)
        studentA.addCourse(externalCourseExecution)
        userRepository.save(studentA)

        def studentB = new Student(USER_3_NAME, USER_3_USERNAME, USER_3_EMAIL, false, AuthUser.Type.TECNICO)
        studentB.addCourse(externalCourseExecution)
        userRepository.save(studentB)

        def studentC = new Student(USER_4_NAME, USER_4_USERNAME, USER_4_EMAIL, false, AuthUser.Type.TECNICO)
        studentC.addCourse(externalCourseExecution)
        userRepository.save(studentC)

        // Create quiz with 4 questions
        def question1 = new Question()
        question1.setKey(1)
        question1.setTitle(QUESTION_1_TITLE)
        question1.setContent(QUESTION_1_CONTENT)
        question1.setCourse(externalCourse)
        question1.setNumberOfAnswers(2)
        question1.setNumberOfCorrect(1)
        question1.setStatus(Question.Status.AVAILABLE)
        def questionDetails1 = new MultipleChoiceQuestion()
        question1.setQuestionDetails(questionDetails1)
        questionDetailsRepository.save(questionDetails1)
        questionRepository.save(question1)

        def option1 = new Option()
        option1.setContent(OPTION_1_CONTENT)
        option1.setCorrect(true)
        option1.setSequence(0)
        option1.setQuestionDetails(questionDetails1)
        optionRepository.save(option1)

        def option2 = new Option()
        option2.setContent(OPTION_2_CONTENT)
        option2.setCorrect(false)
        option2.setSequence(1)
        option2.setQuestionDetails(questionDetails1)
        optionRepository.save(option2)

        def question2 = new Question()
        question2.setKey(2)
        question2.setTitle(QUESTION_2_TITLE)
        question2.setContent(QUESTION_2_CONTENT)
        question2.setCourse(externalCourse)
        question2.setNumberOfAnswers(2)
        question2.setNumberOfCorrect(1)
        question2.setStatus(Question.Status.AVAILABLE)
        def questionDetails2 = new MultipleChoiceQuestion()
        question2.setQuestionDetails(questionDetails2)
        questionDetailsRepository.save(questionDetails2)
        questionRepository.save(question2)

        def option3 = new Option()
        option3.setContent(OPTION_1_CONTENT)
        option3.setCorrect(true)
        option3.setSequence(0)
        option3.setQuestionDetails(questionDetails2)
        optionRepository.save(option3)

        def option4 = new Option()
        option4.setContent(OPTION_2_CONTENT)
        option4.setCorrect(false)
        option4.setSequence(1)
        option4.setQuestionDetails(questionDetails2)
        optionRepository.save(option4)

        def question3 = new Question()
        question3.setKey(3)
        question3.setTitle(QUESTION_3_TITLE)
        question3.setContent(QUESTION_3_CONTENT)
        question3.setCourse(externalCourse)
        question3.setNumberOfAnswers(2)
        question3.setNumberOfCorrect(1)
        question3.setStatus(Question.Status.AVAILABLE)
        def questionDetails3 = new MultipleChoiceQuestion()
        question3.setQuestionDetails(questionDetails3)
        questionDetailsRepository.save(questionDetails3)
        questionRepository.save(question3)

        def option5 = new Option()
        option5.setContent(OPTION_1_CONTENT)
        option5.setCorrect(true)
        option5.setSequence(0)
        option5.setQuestionDetails(questionDetails3)
        optionRepository.save(option5)

        def option6 = new Option()
        option6.setContent(OPTION_2_CONTENT)
        option6.setCorrect(false)
        option6.setSequence(1)
        option6.setQuestionDetails(questionDetails3)
        optionRepository.save(option6)

        def question4 = new Question()
        question4.setKey(4)
        question4.setTitle(QUESTION_4_TITLE)
        question4.setContent(QUESTION_4_CONTENT)
        question4.setCourse(externalCourse)
        question4.setNumberOfAnswers(2)
        question4.setNumberOfCorrect(1)
        question4.setStatus(Question.Status.AVAILABLE)
        def questionDetails4 = new MultipleChoiceQuestion()
        question4.setQuestionDetails(questionDetails4)
        questionDetailsRepository.save(questionDetails4)
        questionRepository.save(question4)

        def option7 = new Option()
        option7.setContent(OPTION_1_CONTENT)
        option7.setCorrect(true)
        option7.setSequence(0)
        option7.setQuestionDetails(questionDetails4)
        optionRepository.save(option7)

        def option8 = new Option()
        option8.setContent(OPTION_2_CONTENT)
        option8.setCorrect(false)
        option8.setSequence(1)
        option8.setQuestionDetails(questionDetails4)
        optionRepository.save(option8)

        def quiz = new Quiz()
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setTitle(QUIZ_TITLE)
        quiz.setKey(1)
        quiz.setCourseExecution(externalCourseExecution)
        quizRepository.save(quiz)

        def quizQuestion1 = new QuizQuestion()
        quizQuestion1.setQuiz(quiz)
        quizQuestion1.setQuestion(question1)
        quizQuestionRepository.save(quizQuestion1)

        def quizQuestion2 = new QuizQuestion()
        quizQuestion2.setQuiz(quiz)
        quizQuestion2.setQuestion(question2)
        quizQuestionRepository.save(quizQuestion2)

        def quizQuestion3 = new QuizQuestion()
        quizQuestion3.setQuiz(quiz)
        quizQuestion3.setQuestion(question3)
        quizQuestionRepository.save(quizQuestion3)

        def quizQuestion4 = new QuizQuestion()
        quizQuestion4.setQuiz(quiz)
        quizQuestion4.setQuestion(question4)
        quizQuestionRepository.save(quizQuestion4)

        // Student A has 100% correct answers
        def quizAnswerA = new QuizAnswer()
        quizAnswerA.setStudent(studentA)
        quizAnswerA.setQuiz(quiz)
        quizAnswerA.setCompleted(true)
        quizAnswerRepository.save(quizAnswerA)

        def questionAnswerA1 = new QuestionAnswer()
        def answerDetailsA1 = new MultipleChoiceAnswer(questionAnswerA1, option1)
        questionAnswerA1.setAnswerDetails(answerDetailsA1)
        questionAnswerA1.setQuizAnswer(quizAnswerA)
        questionAnswerA1.setQuizQuestion(quizQuestion1)
        questionAnswerRepository.save(questionAnswerA1)
        answerDetailsRepository.save(answerDetailsA1)

        def questionAnswerA2 = new QuestionAnswer()
        def answerDetailsA2 = new MultipleChoiceAnswer(questionAnswerA2, option3)
        questionAnswerA2.setAnswerDetails(answerDetailsA2)
        questionAnswerA2.setQuizAnswer(quizAnswerA)
        questionAnswerA2.setQuizQuestion(quizQuestion2)
        questionAnswerRepository.save(questionAnswerA2)
        answerDetailsRepository.save(answerDetailsA2)

        def questionAnswerA3 = new QuestionAnswer()
        def answerDetailsA3 = new MultipleChoiceAnswer(questionAnswerA3, option5)
        questionAnswerA3.setAnswerDetails(answerDetailsA3)
        questionAnswerA3.setQuizAnswer(quizAnswerA)
        questionAnswerA3.setQuizQuestion(quizQuestion3)
        questionAnswerRepository.save(questionAnswerA3)
        answerDetailsRepository.save(answerDetailsA3)

        def questionAnswerA4 = new QuestionAnswer()
        def answerDetailsA4 = new MultipleChoiceAnswer(questionAnswerA4, option7)
        questionAnswerA4.setAnswerDetails(answerDetailsA4)
        questionAnswerA4.setQuizAnswer(quizAnswerA)
        questionAnswerA4.setQuizQuestion(quizQuestion4)
        questionAnswerRepository.save(questionAnswerA4)
        answerDetailsRepository.save(answerDetailsA4)

        // Student B has exactly 75% correct answers and 25% unanswered
        def quizAnswerB = new QuizAnswer()
        quizAnswerB.setStudent(studentB)
        quizAnswerB.setQuiz(quiz)
        quizAnswerB.setCompleted(true)
        quizAnswerRepository.save(quizAnswerB)

        def questionAnswerB1 = new QuestionAnswer()
        def answerDetailsB1 = new MultipleChoiceAnswer(questionAnswerB1, option1)
        questionAnswerB1.setAnswerDetails(answerDetailsB1)
        questionAnswerB1.setQuizAnswer(quizAnswerB)
        questionAnswerB1.setQuizQuestion(quizQuestion1)
        questionAnswerRepository.save(questionAnswerB1)
        answerDetailsRepository.save(answerDetailsB1)

        def questionAnswerB2 = new QuestionAnswer()
        def answerDetailsB2 = new MultipleChoiceAnswer(questionAnswerB2, option3)
        questionAnswerB2.setAnswerDetails(answerDetailsB2)
        questionAnswerB2.setQuizAnswer(quizAnswerB)
        questionAnswerB2.setQuizQuestion(quizQuestion2)
        questionAnswerRepository.save(questionAnswerB2)
        answerDetailsRepository.save(answerDetailsB2)

        def questionAnswerB3 = new QuestionAnswer()
        def answerDetailsB3 = new MultipleChoiceAnswer(questionAnswerB3, option5)
        questionAnswerB3.setAnswerDetails(answerDetailsB3)
        questionAnswerB3.setQuizAnswer(quizAnswerB)
        questionAnswerB3.setQuizQuestion(quizQuestion3)
        questionAnswerRepository.save(questionAnswerB3)
        answerDetailsRepository.save(answerDetailsB3)

        // Student C has exactly 75% correct answers and 25% wrong
        def quizAnswerC = new QuizAnswer()
        quizAnswerC.setStudent(studentC)
        quizAnswerC.setQuiz(quiz)
        quizAnswerC.setCompleted(true)
        quizAnswerRepository.save(quizAnswerC)

        def questionAnswerC1 = new QuestionAnswer()
        def answerDetailsC1 = new MultipleChoiceAnswer(questionAnswerC1, option1)
        questionAnswerC1.setAnswerDetails(answerDetailsC1)
        questionAnswerC1.setQuizAnswer(quizAnswerC)
        questionAnswerC1.setQuizQuestion(quizQuestion1)
        questionAnswerRepository.save(questionAnswerC1)
        answerDetailsRepository.save(answerDetailsC1)

        def questionAnswerC2 = new QuestionAnswer()
        def answerDetailsC2 = new MultipleChoiceAnswer(questionAnswerC2, option3)
        questionAnswerC2.setAnswerDetails(answerDetailsC2)
        questionAnswerC2.setQuizAnswer(quizAnswerC)
        questionAnswerC2.setQuizQuestion(quizQuestion2)
        questionAnswerRepository.save(questionAnswerC2)
        answerDetailsRepository.save(answerDetailsC2)

        def questionAnswerC3 = new QuestionAnswer()
        def answerDetailsC3 = new MultipleChoiceAnswer(questionAnswerC3, option5)
        questionAnswerC3.setAnswerDetails(answerDetailsC3)
        questionAnswerC3.setQuizAnswer(quizAnswerC)
        questionAnswerC3.setQuizQuestion(quizQuestion3)
        questionAnswerRepository.save(questionAnswerC3)
        answerDetailsRepository.save(answerDetailsC3)

        def questionAnswerC4 = new QuestionAnswer()
        def answerDetailsC4 = new MultipleChoiceAnswer(questionAnswerC4, option8)
        questionAnswerC4.setAnswerDetails(answerDetailsC4)
        questionAnswerC4.setQuizAnswer(quizAnswerC)
        questionAnswerC4.setQuizQuestion(quizQuestion4)
        questionAnswerRepository.save(questionAnswerC4)
        answerDetailsRepository.save(answerDetailsC4)
    }

    def "Get number of students with more than 75% correct questions"() {
        given:
        def teacherDashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacherDashboardRepository.save(teacherDashboard)
        def studentStats = new StudentStats(externalCourseExecution, teacherDashboard)
        studentStatsRepository.save(studentStats)

        when:
        teacherDashboard.update()

        then:
        teacherDashboardRepository.findById(teacherDashboard.getId()).get().getStudentStats().size() == 1
        teacherDashboardRepository.findById(teacherDashboard.getId()).get().getStudentStats().contains(studentStats)
        teacherDashboardRepository.findById(teacherDashboard.getId()).get().getStudentStats().each {
            it.getNumMore75CorrectQuestions() == 1
        }
        studentStatsRepository.findById(studentStats.getId()).get().getNumMore75CorrectQuestions() == 1
        studentStatsRepository.findById(studentStats.getId()).get().getTeacherDashboard() == teacherDashboard
        studentStatsRepository.findById(studentStats.getId()).get().getCourseExecution() == externalCourseExecution
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}