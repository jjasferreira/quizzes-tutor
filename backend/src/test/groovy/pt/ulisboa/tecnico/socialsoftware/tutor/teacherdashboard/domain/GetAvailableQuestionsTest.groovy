package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain
//package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthDemoUser
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthExternalUser
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthTecnicoUser
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import spock.lang.Unroll
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question

import java.time.LocalDateTime

@DataJpaTest
class GetAvailableQuestionsTest extends SpockTest{
    def dashboard;
    def course;

    def question1
    def question2
    def question3
    def courseExecution

    def setup(){
        createExternalCourseAndExecution()

        courseExecution = new CourseExecution(externalCourse, COURSE_2_ACRONYM, COURSE_2_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)

        question1 = new Question()
        question1.setCourse(externalCourse)
        question1.setStatus(Question.Status.AVAILABLE)

        question2 = new Question()
        question2.setStatus(Question.Status.AVAILABLE)
        question2.setCourse(externalCourse)

        question3 = new Question()
        question3.setStatus(Question.Status.AVAILABLE)
        question3.setCourse(externalCourse)

        externalCourse.addQuestion(question1)
        externalCourse.addQuestion(question2)
        externalCourse.addQuestion(question3)

        def teacher = new Teacher(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        teacher.addCourse(courseExecution)

    }

    def "getNumberOfQuestions" (){
        given:
            QuestionStats stats = new QuestionStats(courseExecution)
        when:
            stats.update()
        then:
            stats.getNumAvailable() == 3

    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {  }

}