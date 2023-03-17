package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import spock.lang.Unroll

import java.time.LocalDateTime

@DataJpaTest
class TeacherDashboardStudentStatsTest extends SpockTest {
    def teacher

    public static Integer LATEST_EXECUTION_YEAR = 2020

    def setup() {
        teacher = new Teacher(USER_1_NAME, false)
        userRepository.save(teacher)
    }

    @Unroll
    def "When creating a dashboard, student statistics should also be created"() {
        when: "a course and course executions are created"
        def course_name = "Course " + courseExecutions.toString()
        def course_acronym = "C" + courseExecutions.toString()
        def course = new Course(course_name, Course.Type.TECNICO)
        courseRepository.save(course)
        def courseExecutionId = 0

        for (int i = 0; i <= courseExecutions; i++) {
            def academic_term = "1 Semestre " + (LATEST_EXECUTION_YEAR - i).toString() + "/" + (LATEST_EXECUTION_YEAR - i + 1).toString();
            def courseExecution = new CourseExecution(course, course_acronym, academic_term, Course.Type.TECNICO,
                    LocalDateTime.now().withYear(LATEST_EXECUTION_YEAR - i + 1).withMonth(6).withDayOfMonth(30),
            )
            teacher.addCourse(courseExecution)
            courseExecutionRepository.save(courseExecution)

            if (i == 1) {
                courseExecutionId = courseExecution.getId()
            }
        }

        def statsCount = Math.min(courseExecutions, 3)

        and: "a dashboard is created"
        teacherDashboardService.createTeacherDashboard(courseExecutionId, teacher.getId())

        then: "a dashboard is created with student statistics"
        teacherDashboardRepository.findAll().size() == 1;
        teacherDashboardRepository.findAll().get(0).getStudentStats().size() == Math.min(courseExecutions, statsCount);

        and: "student statistics are created for the last 3 course executions"
        studentStatsRepository.findAll().size() == statsCount;
        for (int i = 0; i < statsCount; i++) {
            assert studentStatsRepository.findAll().find { it.getCourseExecution().getEndDate().getYear() == LATEST_EXECUTION_YEAR - i } != null;
        }

        where:
        courseExecutions << [1, 2, 3, 4, 5]
    }

    @Unroll
    def "When deleting a dashboard, student statistics should also be deleted"() {
        when: "a course and course executions are created"
        def course_name = "Course " + courseExecutions.toString()
        def course_acronym = "C" + courseExecutions.toString()
        def course = new Course(course_name, Course.Type.TECNICO)
        courseRepository.save(course)
        def courseExecutionId = 0

        for (int i = 0; i <= courseExecutions; i++) {
            def academic_term = "1 Semestre " + (LATEST_EXECUTION_YEAR - i).toString() + "/" + (LATEST_EXECUTION_YEAR - i + 1).toString();
            def courseExecution = new CourseExecution(course, course_acronym, academic_term, Course.Type.TECNICO,
                    LocalDateTime.now().withYear(LATEST_EXECUTION_YEAR - i + 1).withMonth(6).withDayOfMonth(30),
            )
            teacher.addCourse(courseExecution)
            courseExecutionRepository.save(courseExecution)

            if (i == 1) {
                courseExecutionId = courseExecution.getId()
            }
        }

        and: "a dashboard is created"
        def teacherDashboardDto = teacherDashboardService.createTeacherDashboard(courseExecutionId, teacher.getId())

        and: "a dashboard is deleted"
        teacherDashboardService.removeTeacherDashboard(teacherDashboardDto.getId());

        then: "no dashboards are found"
        teacherDashboardRepository.findAll().size() == 0;

        and: "no student statistics are found"
        studentStatsRepository.findAll().size() == 0;

        where:
        // We are testing different numbers of course executions to catch on possible weird behaviours
        courseExecutions << [1, 2, 3, 4, 5]
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
