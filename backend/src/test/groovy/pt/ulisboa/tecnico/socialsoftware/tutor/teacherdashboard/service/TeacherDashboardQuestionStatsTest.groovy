package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.QuestionStatsRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.TeacherRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler

@DataJpaTest
class TeacherDashboardQuestionStatsTest extends SpockTest {

    @Autowired
    TeacherRepository teacherRepository
    
    @Autowired
    QuestionStatsRepository questionStatsRepository

    def course
    def course2
    def courseExecution
    def courseExecution2
    def courseExecution3
    def courseExecution4
    def courseExecution6
    def teacher

    def setup() {
        course = new Course(COURSE_1_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        course2 = new Course(COURSE_2_NAME, Course.Type.TECNICO)
        courseRepository.save(course2)

        // Create several CourseExecution with different dates ---------------------------------------------------------

        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_2_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(courseExecution)
        
        courseExecution2 = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_YESTERDAY)
        courseExecutionRepository.save(courseExecution2)

        courseExecution3 = new CourseExecution(course2, COURSE_2_ACRONYM, "2 Semestre 2018/2019", Course.Type.TECNICO, LOCAL_DATE_YESTERDAY)
        courseExecutionRepository.save(courseExecution3)

        courseExecution4 = new CourseExecution(course2, COURSE_2_ACRONYM, "1 Semestre 2019/2020", Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(courseExecution4)

        courseExecution6 = new CourseExecution(course2, COURSE_2_ACRONYM, "2 Semestre 2017/2018", Course.Type.TECNICO, LOCAL_DATE_BEFORE)
        courseExecutionRepository.save(courseExecution6)

        
        teacher = new Teacher(USER_1_NAME, false)
        teacherRepository.save(teacher)
    }

    // TESTS -----------------------------------------------------------------------------------------------------------

    def "check questionStats in teacherDashboard"() {

        given: "a teacher in a course execution"
        teacher.addCourse(courseExecution)

        when: "a dashboard is created"
        teacherDashboardService.createTeacherDashboard(courseExecution.getId(), teacher.getId())

        then: "there must be one dashboard"
        teacherDashboardRepository.count() == 1

        and: "as created in setup there is a previous execution"
        def td = teacherDashboardRepository.findAll().get(0)
        td.getQuestionStats().size() == 2

        td.getQuestionStats().get(0).getCourseExecution().getId() == courseExecution.getId()
        td.getQuestionStats().get(1).getCourseExecution().getId() == courseExecution2.getId()
    }

    def "check questionStats with 3 previous excutions"(){
        given: "a teacher in a course execution"
        teacher.addCourse(courseExecution4)

        when: "a dashboard is created"
        teacherDashboardService.createTeacherDashboard(courseExecution4.getId(), teacher.getId())

        then: "there must be one dashboard"
        teacherDashboardRepository.count() == 1

        and: "as created in setup there are 3 previous execution"
        def td = teacherDashboardRepository.findAll().get(0)
        td.getQuestionStats().size() == 3

        and: "executions are ordered"
        td.getQuestionStats().get(0).getCourseExecution().getId() == courseExecution4.getId()
        td.getQuestionStats().get(1).getCourseExecution().getId() == courseExecution3.getId()
        td.getQuestionStats().get(2).getCourseExecution().getId() == courseExecution6.getId()
    }

    def "remove teacherDashboard"() {
        given: "a teacherDashboard"
        teacher.addCourse(courseExecution4)
        teacherDashboardService.createTeacherDashboard(courseExecution4.getId(), teacher.getId())
        
        when: "it is removed"
        def td = teacherDashboardRepository.findAll().get(0)
        teacherDashboardService.removeTeacherDashboard(td.getId())

        then: "there musn't be a teacherDashboard"
        teacherDashboardRepository.findAll().size() == 0
        questionStatsRepository.findAll().size() == 0

    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}

