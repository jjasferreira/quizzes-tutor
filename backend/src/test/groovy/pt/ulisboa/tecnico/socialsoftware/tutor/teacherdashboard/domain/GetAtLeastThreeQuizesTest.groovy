package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.StudentStatsRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher

@DataJpaTest
class GetAtLeastThreeQuizesTest extends SpockTest {
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

        def quizA = new Quiz()
        quizA.setCourseExecution(courseExecutionA)
        quizRepository.save(quizA)

        def quizB = new Quiz()
        quizB.setCourseExecution(courseExecutionA)
        quizRepository.save(quizB)

        def quizC = new Quiz()
        quizC.setCourseExecution(courseExecutionA)
        quizRepository.save(quizC)

        // studentA has 3 completed quizzes
        def quizAnswer1 = new QuizAnswer(studentA, quizA)
        quizAnswer1.setCompleted(true)
        quizAnswerRepository.save(quizAnswer1)

        def quizAnswer2 = new QuizAnswer(studentA, quizB)
        quizAnswer2.setCompleted(true)
        quizAnswerRepository.save(quizAnswer2)

        def quizAnswer3 = new QuizAnswer(studentA, quizC)
        quizAnswer3.setCompleted(true)
        quizAnswerRepository.save(quizAnswer3)

        // studentB has 2 completed quizzes
        def quizAnswer4 = new QuizAnswer(studentB, quizA)
        quizAnswer4.setCompleted(true)
        quizAnswerRepository.save(quizAnswer4)

        def quizAnswer5 = new QuizAnswer(studentB, quizB)
        quizAnswer5.setCompleted(true)
        quizAnswerRepository.save(quizAnswer5)

        def quizAnswer6 = new QuizAnswer(studentB, quizC)
        quizAnswer6.setCompleted(false)
        quizAnswerRepository.save(quizAnswer6)
    }

    def "get number of students with at least 3 completed quizzes"() {
        given:
        def studentStats = studentStatsRepository.findAll().get(0)
        def teacherDashboard = teacherDashboardRepository.findAll().get(0)
        teacherDashboard.addStudentStats(studentStats)

        when:
        teacherDashboard.update()

        then:
        studentStats.getNumAtLeast3Quizzes() == 1
        studentStats.getTeacherDashboard() == teacherDashboard
        studentStats.getCourseExecution() == teacherDashboard.getCourseExecution()
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}