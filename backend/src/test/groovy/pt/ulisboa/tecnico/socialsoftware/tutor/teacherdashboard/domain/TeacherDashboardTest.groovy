package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.*

@DataJpaTest
class TeacherDashboardTest extends SpockTest {
    @Autowired
    QuestionStatsRepository questionStatsRepository

    def dashboard
    def stats
    def setup() {
        createExternalCourseAndExecution()


        def teacher = new Teacher(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL,
                false, AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(externalCourseExecution)
        externalCourseExecution.addUser(teacher)
        userRepository.save(teacher)
        dashboard = new TeacherDashboard(externalCourseExecution, teacher)
        stats = new QuestionStats(externalCourseExecution)
        questionStatsRepository.save(stats)

    }

    def "testStudentDashboard" (){
        given:

        def stat = questionStatsRepository.findAll().get(0)
        dashboard.addQuestionStats(stat)

        def question1 = new Question()
        question1.setCourse(externalCourse)
        question1.setStatus(Question.Status.AVAILABLE)
        externalCourse.addQuestion(question1)


        when:
        dashboard.update()

        then:
        dashboard.getQuestion().numAvailable[0] == 1
        dashboard.toString() != ""

    }



    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {  }
}
