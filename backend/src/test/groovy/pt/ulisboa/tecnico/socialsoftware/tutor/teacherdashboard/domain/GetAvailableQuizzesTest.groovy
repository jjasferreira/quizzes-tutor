package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.QuizStatsRepository

@DataJpaTest
class GetAvailableQuizzesTest extends SpockTest {
    @Autowired
    QuizStatsRepository quizStatsRepository

    def setup() {
        externalCourse = new Course(COURSE_1_NAME, Course.Type.TECNICO)
        courseRepository.save(externalCourse)

        // Test case (1): 3 available quizzes on a QuizStats
        def courseExecutionA = new CourseExecution(externalCourse, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(courseExecutionA)
        def quizStatsA = new QuizStats(courseExecutionA)
        quizStatsRepository.save(quizStatsA)
        def quiz1 = new Quiz()
        quiz1.setCourseExecution(courseExecutionA)
        quizRepository.save(quiz1)
        def quiz2 = new Quiz()
        quiz2.setCourseExecution(courseExecutionA)
        quizRepository.save(quiz2)
        def quiz3 = new Quiz()
        quiz3.setCourseExecution(courseExecutionA)
        quizRepository.save(quiz3)

        // Test case (2): 0 available quizzes on a QuizStats
        def courseExecutionB = new CourseExecution(externalCourse, COURSE_1_ACRONYM, COURSE_2_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(courseExecutionB)
        def quizStatsB = new QuizStats(courseExecutionB)
        quizStatsRepository.save(quizStatsB)
    }

    // Test case (1): 3 available quizzes on a QuizStats
    def "getNumQuizzesWhen3Available"() {
        given:
        def quizStats = quizStatsRepository.findAll().get(0)
        def quiz = quizRepository.findAll().get(0)

        when:
        quizStats.update()

        then:
        quizStats.getNumQuizzes() == 3
        quizStats.getCourseExecution() == quiz.getCourseExecution()
    }

    // Test case (2): 0 available quizzes on a QuizStats
    def "getNumQuizzesWhen0Available"() {
        given:
        def quizStats = quizStatsRepository.findAll().get(1)

        when:
        quizStats.update()

        then:
        quizStats.getNumQuizzes() == 0
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}