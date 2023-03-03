package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.QuizStatsRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.StudentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.TeacherRepository


@DataJpaTest
class GetUniqueQuizzesSolvedTest extends SpockTest {
    @Autowired
    QuizStatsRepository quizStatsRepository
    @Autowired
    StudentRepository studentRepository
    @Autowired
    TeacherRepository teacherRepository

    def setup() {
        externalCourse = new Course(COURSE_1_NAME, Course.Type.TECNICO)
        courseRepository.save(externalCourse)
    }

    // Test case (1): 1 course executions, 0 quizzes, 0 students
    def "test 1 executions 0 quizzes 0 students"() {
        given:
        def courseExecution = new CourseExecution(externalCourse, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(courseExecution)
        // Also test creation of TeacherDashboard
        def teacher = new Teacher()
        teacherRepository.save(teacher)
        def teacherDashboard = new TeacherDashboard(courseExecution, teacher)
        teacherDashboardRepository.save(teacherDashboard)
        def quizStats = new QuizStats(courseExecution, teacherDashboard)
        quizStatsRepository.save(quizStats)

        when:
        quizStats.update()

        then:
        def quizStatsT = quizStatsRepository.findAll().get(0)
        quizStatsT.getCourseExecution() != null
        quizStatsT.getTeacherDashboard() != null
        quizStatsT.getNumQuizzes() == 0
    }

    // Test case (2): 1 course executions, 1 quizzes, 0 students
    def "test 1 executions 1 quizzes 0 students"() {
        given:
        def courseExecution = new CourseExecution(externalCourse, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(courseExecution)
        def quizStats = new QuizStats(courseExecution)
        quizStatsRepository.save(quizStats)
        def quiz = new Quiz()
        quiz.setCourseExecution(courseExecution)
        quizRepository.save(quiz)

        when:
        quizStats.update()

        then:
        def quizStatsT = quizStatsRepository.findAll().get(0)
        def quizT = quizRepository.findAll().get(0)
        quizStatsT.getCourseExecution() == quizT.getCourseExecution()
        quizStatsT.getNumQuizzes() == 1
        quizT.getQuizAnswers().size() == 0
        quizStatsT.getUniqueQuizzesSolved() == 0
    }

    
    // Test case (3): 1 course executions, 1 quizzes, 1 students
    def "test 1 executions 1 quizzes 1 students"() {
        given:
        def courseExecution = new CourseExecution(externalCourse, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(courseExecution)
        def quizStats = new QuizStats(courseExecution)
        quizStatsRepository.save(quizStats)
        def quiz = new Quiz()
        quiz.setCourseExecution(courseExecution)
        def student = new Student()
        studentRepository.save(student)
        def quizAnswer = new QuizAnswer(student, quiz)
        quizAnswer.setCompleted(true)
        quiz.addQuizAnswer(quizAnswer)
        quizRepository.save(quiz)
        quizAnswerRepository.save(quizAnswer)

        when:
        quizStats.update()

        then:
        def quizStatsT = quizStatsRepository.findAll().get(0)
        def quizT = quizRepository.findAll().get(0)
        def quizAnswerT = quizAnswerRepository.findAll().get(0)
        quizStatsT.getCourseExecution() == quiz.getCourseExecution()
        quizStatsT.getNumQuizzes() == 1
        quizT.getQuizAnswers().contains(quizAnswerT)
        quizAnswerT.getStudent() != null
        quizStatsT.getUniqueQuizzesSolved() == 1
    }

    // Test case (4): 1 course executions, 1 quizzes, 2 students
    def "test 1 executions 1 quizzes 2 students"() {
        given:
        def courseExecution = new CourseExecution(externalCourse, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(courseExecution)
        def quizStats = new QuizStats(courseExecution)
        quizStatsRepository.save(quizStats)
        def quiz = new Quiz()
        quiz.setCourseExecution(courseExecution)
        courseExecution.addQuiz(quiz)
        def student1 = new Student()
        studentRepository.save(student1)
        def quizAnswer1 = new QuizAnswer(student1, quiz)
        quizAnswer1.setCompleted(true)
        quiz.addQuizAnswer(quizAnswer1)
        def student2 = new Student()
        studentRepository.save(student2)
        def quizAnswer2 = new QuizAnswer(student2, quiz)
        quizAnswer2.setCompleted(true)
        quiz.addQuizAnswer(quizAnswer2)
        quizRepository.save(quiz)
        quizAnswerRepository.save(quizAnswer1)
        quizAnswerRepository.save(quizAnswer2)

        when:
        quizStats.update()

        then:
        def quizStatsT = quizStatsRepository.findAll().get(0)
        def quizT = quizRepository.findAll().get(0)
        def quizAnswerT1 = quizAnswerRepository.findAll().get(0)
        def quizAnswerT2 = quizAnswerRepository.findAll().get(1)
        quizStatsT.getCourseExecution() == quiz.getCourseExecution()
        quizStatsT.getNumQuizzes() == 1
        quizT.getQuizAnswers().contains(quizAnswerT1)
        quizT.getQuizAnswers().contains(quizAnswerT2)
        quizAnswerT1.getStudent() != null
        quizAnswerT2.getStudent() != null
        quizT.getQuizAnswers().size() == 2
        quizStatsT.getUniqueQuizzesSolved() == 1
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}