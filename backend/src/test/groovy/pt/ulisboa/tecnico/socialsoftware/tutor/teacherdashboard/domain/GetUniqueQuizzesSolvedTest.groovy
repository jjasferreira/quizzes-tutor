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
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.QuizStatsRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.StudentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.TeacherRepository


@DataJpaTest
class GetUniqueQuizzesSolvedTest extends SpockTest {
    @Autowired
    QuizStatsRepository quizStatsRepository
    @Autowired
    StudentRepository studentRepository
    @Autowired
    TeacherRepository teacherRepository

    def setup() {
        externalCourse = new Course(COURSE_1_NAME, Course.Type.TECNICO)
        courseRepository.save(externalCourse)

        // Test case (1): 1 course executions, 0 quizzes, 0 students
        def courseExecutionB = new CourseExecution(externalCourse, "C13", COURSE_2_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(courseExecutionB)
        // Also test creation of TeacherDashboard
        def teacherB = new Teacher()
        teacherRepository.save(teacherB)
        def teacherDashboardB = new TeacherDashboard(courseExecutionB, teacherB)
        teacherDashboardRepository.save(teacherDashboardB)
        def quizStatsB = new QuizStats(courseExecutionB, teacherDashboardB)
        quizStatsRepository.save(quizStatsB)

        // Test case (2): 1 course executions, 1 quizzes, 0 students
        def courseExecutionC = new CourseExecution(externalCourse, "C14", COURSE_2_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(courseExecutionC)
        def quizStatsC = new QuizStats(courseExecutionC)
        quizStatsRepository.save(quizStatsC)
        def quiz1 = new Quiz()
        quiz1.setCourseExecution(courseExecutionC)
        quizRepository.save(quiz1)

        // Test case (3): 1 course executions, 1 quizzes, 1 students
        def courseExecutionD = new CourseExecution(externalCourse, "C15", COURSE_2_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(courseExecutionD)
        def quizStatsD = new QuizStats(courseExecutionD)
        quizStatsRepository.save(quizStatsD)
        def quiz2 = new Quiz()
        quiz2.setCourseExecution(courseExecutionD)
        def studentD = new Student()
        studentRepository.save(studentD)
        def quizAnswerD = new QuizAnswer(studentD, quiz2)
        quizAnswerD.setCompleted(true)
        quiz2.addQuizAnswer(quizAnswerD)
        quizRepository.save(quiz2)
        quizAnswerRepository.save(quizAnswerD)

        // Test case (4): 1 course executions, 1 quizzes, 2 students
        def courseExecutionE = new CourseExecution(externalCourse, "C16", COURSE_2_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(courseExecutionE)
        def quizStatsE = new QuizStats(courseExecutionE)
        quizStatsRepository.save(quizStatsE)
        def quiz3 = new Quiz()
        quiz3.setCourseExecution(courseExecutionE)
        courseExecutionE.addQuiz(quiz3)
        def studentE1 = new Student()
        studentRepository.save(studentE1)
        def quizAnswerE1 = new QuizAnswer(studentE1, quiz3)
        quizAnswerE1.setCompleted(true)
        quiz3.addQuizAnswer(quizAnswerE1)
        def studentE2 = new Student()
        studentRepository.save(studentE2)
        def quizAnswerE2 = new QuizAnswer(studentE2, quiz3)
        quizAnswerE2.setCompleted(true)
        quiz3.addQuizAnswer(quizAnswerE2)
        quizRepository.save(quiz3)
        quizAnswerRepository.save(quizAnswerE1)
        quizAnswerRepository.save(quizAnswerE2)
    }

    // Test case (1): 1 course executions, 0 quizzes, 0 students
    def "test1Executions0Quizzes0Students"() {
        given:
        def quizStats = quizStatsRepository.findAll().get(0)

        when:
        quizStats.update()

        then:
        quizStats.getCourseExecution() != null
        quizStats.getTeacherDashboard() != null
        quizStats.getNumQuizzes() == 0
    }

    // Test case (2): 1 course executions, 1 quizzes, 0 students
    def "test1Executions1Quizzes0Students"() {
        given:
        def quizStats = quizStatsRepository.findAll().get(1)
        def quiz = quizRepository.findAll().get(0)

        when:
        quizStats.update()

        then:
        quizStats.getCourseExecution() == quiz.getCourseExecution()
        quizStats.getNumQuizzes() == 1
        quiz.getQuizAnswers().size() == 0
        quizStats.getUniqueQuizzesSolved() == 0
    }

    
    // Test case (3): 1 course executions, 1 quizzes, 1 students
    def "test1Executions1Quizzes1Students"() {
        given:
        def quizStats = quizStatsRepository.findAll().get(2)
        def quiz = quizRepository.findAll().get(1)
        def quizAnswer = quizAnswerRepository.findAll().get(0)

        when:
        quizStats.update()

        then:
        quizStats.getCourseExecution() == quiz.getCourseExecution()
        quizStats.getNumQuizzes() == 1
        quiz.getQuizAnswers().contains(quizAnswer)
        quizAnswer.getStudent() != null
        quizStats.getUniqueQuizzesSolved() == 1
    }

    // Test case (4): 1 course executions, 1 quizzes, 2 students
    def "test1Executions1Quizzes2Students"() {
        given:
        def quizStats = quizStatsRepository.findAll().get(3)
        def quiz = quizRepository.findAll().get(2)
        def quizAnswer1 = quizAnswerRepository.findAll().get(1)
        def quizAnswer2 = quizAnswerRepository.findAll().get(2)

        when:
        quizStats.update()

        then:
        quizStats.getCourseExecution() == quiz.getCourseExecution()
        quizStats.getNumQuizzes() == 1
        quiz.getQuizAnswers().contains(quizAnswer1)
        quiz.getQuizAnswers().contains(quizAnswer2)
        quizAnswer1.getStudent() != null
        quizAnswer2.getStudent() != null
        quiz.getQuizAnswers().size() == 2
        quizStats.getUniqueQuizzesSolved() == 1
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}