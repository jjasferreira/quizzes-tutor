package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import spock.lang.Unroll

import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.QuizStatsRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.QuizStatsRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.TeacherRepository


@DataJpaTest
class TeacherDashboardQuizStatsTest extends SpockTest {
    @Autowired
    TeacherRepository teacherRepository
    @Autowired
    QuizStatsRepository quizStatsRepository

    def course
    def courseExecution
    def courseExecution2
    def courseExecution3
    def courseExecution4
    def teacher

    def setup() {
        course = new Course(COURSE_1_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_2_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(courseExecution)
        courseExecution2 = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_YESTERDAY)
        courseExecutionRepository.save(courseExecution2)
        teacher = new Teacher(USER_1_NAME, false)
        teacherRepository.save(teacher)
    }

    def "have only 1 previous execution associated with the course of the active execution"() {
        given: "a teacher in a course execution"
        teacher.addCourse(courseExecution)

        when: "a dashboard is created"
        teacherDashboardService.createTeacherDashboard(courseExecution.getId(), teacher.getId())

        then: "the dashboard has 2 QuizStats objects for all course executions"
        teacherDashboardRepository.count() == 1L
        def td = teacherDashboardRepository.findAll().get(0)
        td.getQuizStats().size() == 2
        td.getQuizStats().get(0).getCourseExecution().getId() == courseExecution.getId()
        td.getQuizStats().get(1).getCourseExecution().getId() == courseExecution2.getId()
    }

    def "create 2 more executions associated with that same course" () {
        given: "two more previous course executions"
        teacher.addCourse(courseExecution)
        courseExecution3 = new CourseExecution(course, COURSE_1_ACRONYM, "2 Semestre 2018/2019", Course.Type.TECNICO, LOCAL_DATE_BEFORE)
        courseExecution4 = new CourseExecution(course, COURSE_1_ACRONYM, "1 Semestre 2018/2019", Course.Type.TECNICO, LOCAL_DATE_BEFORE.minusDays(1))

        when: "a dashboard is created"
        teacherDashboardService.createTeacherDashboard(courseExecution.getId(), teacher.getId())

        then: "the dashboard has 3 QuizStats objects even though there are 4 executions"
        teacherDashboardRepository.count() == 1L
        def td = teacherDashboardRepository.findAll().get(0)
        td.getQuizStats().size() == 3
        td.getQuizStats().get(0).getCourseExecution().getId() == courseExecution.getId()
        td.getQuizStats().get(1).getCourseExecution().getId() == courseExecution2.getId()
        td.getQuizStats().get(2).getCourseExecution().getId() == courseExecution3.getId()

    }

    @Unroll
    def "update teacher dashboard with invalid ID" () {
        when: "incorrect dashboard ID is given to updateTeacherDashboard"
        teacherDashboardService.updateTeacherDashboard(dashboardId)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.DASHBOARD_NOT_FOUND

        where:
        dashboardId << [null, 10, -1]
    }

    def "update teacher dashboard with valid ID" () {
        given: "teacher dashboard is created and its ID is given to updateTeacherDashboard"
        teacher.addCourse(courseExecution)
        teacherDashboardService.createTeacherDashboard(courseExecution.getId(), teacher.getId())
        def quiz = new Quiz()
        quiz.setCourseExecution(courseExecution)
        quizRepository.save(quiz)

        when: "the teacher dashboard is updated"
        def td = teacherDashboardRepository.findAll().get(0)
        teacherDashboardService.updateTeacherDashboard(td.getId())

        then: "the dashboard is updated and the QuizStats are updated"
        teacherDashboardRepository.count() == 1L
        quizStatsRepository.count() == 2L
        def quizStats = quizStatsRepository.findAll().get(0)
        quizStats == td.getQuizStats().get(0)
        quizStats.getNumQuizzes() == 1
    }

    def "update all teacher dashboards" () {
        given: "one teacher in two course executions of different courses, with one having a quiz"
        def courseAlt = new Course(COURSE_2_NAME, Course.Type.TECNICO)
        courseRepository.save(courseAlt)
        def courseExecutionAlt = new CourseExecution(courseAlt, COURSE_2_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(courseExecutionAlt)
        teacher.addCourse(courseExecution)
        teacher.addCourse(courseExecutionAlt)
        teacherDashboardService.createTeacherDashboard(courseExecution.getId(), teacher.getId())
        teacherDashboardService.createTeacherDashboard(courseExecutionAlt.getId(), teacher.getId())
        // Add a quiz to one of the course executions but not the other to test updateAllTeacherDashboards
        def quiz = new Quiz()
        quiz.setCourseExecution(courseExecution)
        quizRepository.save(quiz)

        when: "the teacher dashboards are updated"
        teacherDashboardService.updateAllTeacherDashboards()

        then: "only one dashboard has changed"
        teacherDashboardRepository.count() == 2L
        def td = teacherDashboardRepository.findAll().get(0)
        def tdAlt = teacherDashboardRepository.findAll().get(1)
        quizStatsRepository.count() == 3L
        def quizStats1 = quizStatsRepository.findAll().get(0)
        def quizStats3 = quizStatsRepository.findAll().get(2)
        quizStats1 == td.getQuizStats().get(0)
        quizStats1.getNumQuizzes() == 1
        quizStats3 == tdAlt.getQuizStats().get(0)
        quizStats3.getNumQuizzes() == 0
    }

    def "remove teacher dashboard and check for QuizStats" () {
        given: "a teacher in a course execution"
        teacher.addCourse(courseExecution)
        teacherDashboardService.createTeacherDashboard(courseExecution.getId(), teacher.getId())

        when: "the user removes the dashboard"
        def td = teacherDashboardRepository.findAll().get(0)
        teacherDashboardService.removeTeacherDashboard(td.getId())

        then: "the dashboard is removed and the QuizStats are removed"

        teacherDashboardRepository.count() == 0L
        quizStatsRepository.count() == 0L
    }

    def "remove teacher dashboard and check for QuizStats" () {
        given: "a teacher in a course execution"
        teacher.addCourse(courseExecution)
        teacherDashboardService.createTeacherDashboard(courseExecution.getId(), teacher.getId())

        when: "the user removes the dashboard"
        def td = teacherDashboardRepository.findAll().get(0)
        teacherDashboardService.removeTeacherDashboard(td.getId())

        then: "the dashboard is removed and the QuizStats are removed"

        teacherDashboardRepository.count() == 0L
        quizStatsRepository.count() == 0L
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}