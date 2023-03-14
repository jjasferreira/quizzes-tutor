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

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.QuizStatsRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.TeacherRepository

@DataJpaTest
class GetAvailableQuizzesTest extends SpockTest {
    @Autowired
    QuizStatsRepository quizStatsRepository
    @Autowired
    TeacherRepository teacherRepository

    def setup() {
        externalCourse = new Course(COURSE_1_NAME, Course.Type.TECNICO)
        courseRepository.save(externalCourse)
    }

    // Test case (1): 3 available quizzes on a QuizStats
    def "test 3 available quizzes quizstats"() {
        given:
        def courseExecution = new CourseExecution(externalCourse, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(courseExecution)
        def teacher = new Teacher()
        teacherRepository.save(teacher)
        def teacherDashboard = new TeacherDashboard(courseExecution, teacher)
        def quizStats = new QuizStats(courseExecution, teacherDashboard)
        quizStatsRepository.save(quizStats)
        def quiz1 = new Quiz()
        quiz1.setCourseExecution(courseExecution)
        quizRepository.save(quiz1)
        def quiz2 = new Quiz()
        quiz2.setCourseExecution(courseExecution)
        quizRepository.save(quiz2)
        def quiz3 = new Quiz()
        quiz3.setCourseExecution(courseExecution)
        quizRepository.save(quiz3)

        when:
        quizStats.update()

        then:
        def quizStatsT = quizStatsRepository.findAll().get(0)
        def quizT = quizRepository.findAll().get(0)
        quizStatsT.getNumQuizzes() == 3
        quizStatsT.getCourseExecution() == quizT.getCourseExecution()
    }

    // Test case (2): 0 available quizzes on a QuizStats
    def "test 0 available quizzes quizstats"() {
        given:
        def courseExecution = new CourseExecution(externalCourse, COURSE_1_ACRONYM, COURSE_2_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(courseExecution)
        def teacher = new Teacher()
        teacherRepository.save(teacher)
        def teacherDashboard = new TeacherDashboard(courseExecution, teacher)
        def quizStats = new QuizStats(courseExecution, teacherDashboard)
        quizStatsRepository.save(quizStats)

        when:
        quizStats.update()

        then:
        def quizStatsT = quizStatsRepository.findAll().get(0)
        quizStatsT.getNumQuizzes() == 0
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}