package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher

@DataJpaTest
class GetAtLeastThreeQuizzesTest extends SpockTest {
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

        // Create quizzes A, B and C
        def quizA = new Quiz()
        quizA.setCourseExecution(externalCourseExecution)
        quizRepository.save(quizA)

        def quizB = new Quiz()
        quizB.setCourseExecution(externalCourseExecution)
        quizRepository.save(quizB)

        def quizC = new Quiz()
        quizC.setCourseExecution(externalCourseExecution)
        quizRepository.save(quizC)

        // Student A has 3 completed quizzes
        def quizAnswer1 = new QuizAnswer(studentA, quizA)
        quizAnswer1.setCompleted(true)
        quizAnswerRepository.save(quizAnswer1)

        def quizAnswer2 = new QuizAnswer(studentA, quizB)
        quizAnswer2.setCompleted(true)
        quizAnswerRepository.save(quizAnswer2)

        def quizAnswer3 = new QuizAnswer(studentA, quizC)
        quizAnswer3.setCompleted(true)
        quizAnswerRepository.save(quizAnswer3)

        // Student B has 2 completed quizzes and 1 incomplete quiz
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

    def "Get number of students with at least 3 completed quizzes"() {
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
            it.getNumAtLeast3Quizzes() == 1
        }
        studentStatsRepository.findById(studentStats.getId()).get().getNumAtLeast3Quizzes() == 1
        studentStatsRepository.findById(studentStats.getId()).get().getTeacherDashboard() == teacherDashboard
        studentStatsRepository.findById(studentStats.getId()).get().getCourseExecution() == externalCourseExecution
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}