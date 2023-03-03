package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.QuizStatsRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.StudentRepository

import static org.junit.Assert.assertEquals

@DataJpaTest
class GetAverageQuizzesSolvedTest extends SpockTest {
    @Autowired
    QuizStatsRepository quizStatsRepository
    @Autowired
    StudentRepository studentRepository

    def setup() {
        externalCourse = new Course(COURSE_1_NAME, Course.Type.TECNICO)
        courseRepository.save(externalCourse)
    }

    def "test 1 executions 0 quizzes 0 students 0 quizAnswers"() {
        given:
        def courseExecution = new CourseExecution(externalCourse, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(courseExecution)
        def quizStats = new QuizStats(courseExecution)
        quizStatsRepository.save(quizStats)

        when:
        quizStats.update()

        then:
        def quizStatsT = quizStatsRepository.findAll().get(0)
        quizStatsT.getCourseExecution() != null
        quizStatsT.getAverageQuizzesSolved() == 0
        // Also test toString()
        quizStatsT.toString() != null
    }

    def "test 1 executions 0 quizzes 1 students 0 quizAnswers"() {
        given:
        def courseExecution = new CourseExecution(externalCourse, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        def student = new Student("João Cardoso", true)
        studentRepository.save(student)
        courseExecution.addUser(student)
        courseExecutionRepository.save(courseExecution)
        def quizStats = new QuizStats(courseExecution)
        quizStatsRepository.save(quizStats)

        when:
        quizStats.update()

        then:
        def quizStatsT = quizStatsRepository.findAll().get(0)
        quizStatsT.getCourseExecution() != null
        quizStatsT.getAverageQuizzesSolved() == 0
    }

    // 2 students have solved on average 1.5 quizzes
    def "test 1 executions 2 quizzes 2 students 3 quizAnswers"() {
        given:
        def courseExecution = new CourseExecution(externalCourse, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        def student1 = new Student("José João Ferreira", true)
        studentRepository.save(student1)
        courseExecution.addUser(student1)
        def student2 = new Student("José João Ferreira", true)
        studentRepository.save(student2)
        courseExecution.addUser(student2)
        courseExecutionRepository.save(courseExecution)
        def quizStats = new QuizStats(courseExecution)
        quizStatsRepository.save(quizStats)
        def quiz1 = new Quiz()
        quiz1.setCourseExecution(courseExecution)
        quizRepository.save(quiz1)
        def quiz2 = new Quiz()
        quiz2.setCourseExecution(courseExecution)
        quizRepository.save(quiz2)
        // Student 2 has solved 1 quiz and Student 3 has solved 2 quizzes
        def quizAnswer1 = new QuizAnswer(student1, quiz1)
        quizAnswer1.setCompleted(true)
        quizAnswerRepository.save(quizAnswer1)
        def quizAnswer2 = new QuizAnswer(student2, quiz1)
        quizAnswer2.setCompleted(true)
        quizAnswerRepository.save(quizAnswer2)
        def quizAnswer3 = new QuizAnswer(student2, quiz2)
        quizAnswer3.setCompleted(true)
        quizAnswerRepository.save(quizAnswer3)

        when:
        quizStats.update()

        then:
        def quizStatsT = quizStatsRepository.findAll().get(0)
        quizStatsT.getCourseExecution() != null
        assertEquals(quizStatsT.getAverageQuizzesSolved(), 1.5, 0.0001)
    }

    @TestConfiguration
    static class GetAverageQuizzesSolvedTestContextConfiguration extends BeanConfiguration {}
}