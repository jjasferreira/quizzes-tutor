package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
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

        then: "the dashboard has 1 QuizStats object for the course execution"

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

    def "remove teacher dashboard and check for QuizStats" () {
        given: "a teacher in a course execution"
        teacher.addCourse(courseExecution)
        teacherDashboardService.createTeacherDashboard(courseExecution.getId(), teacher.getId())

        when: "the user removes the dashboard"
        def td = teacherDashboardRepository.findAll().get(0)
        teacherDashboardService.removeTeacherDashboard(td.getId())

        then: "the dashboard is removed and the QuizStats are removed"

        teacherDashboardRepository.findAll().size() == 0L
        quizStatsRepository.findAll().size() == 0L
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}