package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.QuizStatsRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.StudentStatsRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher

@DataJpaTest
class GetTotalStudentsTest extends SpockTest {
    @Autowired
    StudentStatsRepository studentStatsRepository

    CourseExecution courseExecutionA;
    Teacher teacherA;

    def setup() {
        externalCourse = new Course(COURSE_1_NAME, Course.Type.TECNICO)
        courseRepository.save(externalCourse)

        courseExecutionA = new CourseExecution(externalCourse, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(courseExecutionA)

        teacherA = new Teacher(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        teacherA.addCourse(courseExecutionA)
        userRepository.save(teacherA)

        def teacherDashboardA = new TeacherDashboard(courseExecutionA, teacherA)
        teacherDashboardRepository.save(teacherDashboardA)

        def studentStatsA = new StudentStats(courseExecutionA)
        studentStatsRepository.save(studentStatsA)
        studentStatsA.setTeacherDashboard(teacherDashboardA)

        def studentA = new Student(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, false, AuthUser.Type.TECNICO)
        studentA.addCourse(courseExecutionA)
        userRepository.save(studentA)

        def studentB = new Student(USER_3_NAME,  USER_3_USERNAME, USER_3_EMAIL, false, AuthUser.Type.TECNICO)
        studentB.addCourse(courseExecutionA)
        userRepository.save(studentB)
    }

    def "add and get StudentStats from TeacherDashboard"() {
        given:
        def studentStats = new StudentStats(courseExecutionA)
        def teacherDashboard = new TeacherDashboard(courseExecutionA, teacherA)

        when:
        teacherDashboard.addStudentStats(studentStats)

        then:
        teacherDashboard.getStudentStats().size() == 1
        teacherDashboard.getStudentStats().contains(studentStats)
    }


    def "get total students through teacherDashboard"() {
        given:
        def studentStats = studentStatsRepository.findAll().get(0)
        def teacherDashboard = teacherDashboardRepository.findAll().get(0)
        teacherDashboard.addStudentStats(studentStats)

        when:
        teacherDashboard.update()

        then:
        studentStats.getNumStudents() == 2
        studentStats.getTeacherDashboard() == teacherDashboard
        studentStats.getCourseExecution() == teacherDashboard.getCourseExecution()
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}