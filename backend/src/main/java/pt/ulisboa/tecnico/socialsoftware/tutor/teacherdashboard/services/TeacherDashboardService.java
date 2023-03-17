package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.services;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.StudentStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto.TeacherDashboardDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.StudentStatsRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.TeacherDashboardRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.TeacherRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuestionStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuizStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.QuestionStatsRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.QuizStatsRepository;

import java.util.*;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class TeacherDashboardService {

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private TeacherDashboardRepository teacherDashboardRepository;

    @Autowired
    private QuizStatsRepository quizStatsRepository;

    @Autowired
    private StudentStatsRepository studentStatsRepository;

    @Autowired
    private QuestionStatsRepository questionStatsRepository;

    private CourseExecution[] getLastTwoCourseExecutions(CourseExecution courseExecution){
        CourseExecution[] courseExecutions = courseExecution.getCourse().getCourseExecutions().stream()
                .filter(x -> x.getEndDate().isBefore(courseExecution.getEndDate()))
                .sorted(Comparator.comparing(CourseExecution::getEndDate).reversed())
                .toArray(CourseExecution[]::new);

        if (courseExecutions.length > 2){
            courseExecutions = Arrays.copyOfRange(courseExecutions, 0, 2);
        }
        return courseExecutions;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TeacherDashboardDto getTeacherDashboard(int courseExecutionId, int teacherId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, teacherId));

        if (!teacher.getCourseExecutions().contains(courseExecution))
            throw new TutorException(TEACHER_NO_COURSE_EXECUTION);

        Optional<TeacherDashboard> dashboardOptional = teacher.getDashboards().stream()
                .filter(dashboard -> dashboard.getCourseExecution().getId().equals(courseExecutionId))
                .findAny();

        return dashboardOptional.
                map(TeacherDashboardDto::new).
                orElseGet(() -> createAndReturnTeacherDashboardDto(courseExecution, teacher));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TeacherDashboardDto createTeacherDashboard(int courseExecutionId, int teacherId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, teacherId));

        if (teacher.getDashboards().stream().anyMatch(dashboard -> dashboard.getCourseExecution().equals(courseExecution)))
            throw new TutorException(TEACHER_ALREADY_HAS_DASHBOARD);

        if (!teacher.getCourseExecutions().contains(courseExecution))
            throw new TutorException(TEACHER_NO_COURSE_EXECUTION);

        return createAndReturnTeacherDashboardDto(courseExecution, teacher);
    }

    private TeacherDashboardDto createAndReturnTeacherDashboardDto(CourseExecution courseExecution, Teacher teacher) {
        TeacherDashboard teacherDashboard = new TeacherDashboard(courseExecution, teacher);
        CourseExecution[] courseExecutions = getLastTwoCourseExecutions(courseExecution);

        // Create a QuizStats object for the last three course executions (if they exist) and add to the dashboard
        QuizStats quizStats = new QuizStats(courseExecution, teacherDashboard);
        teacherDashboard.addQuizStats(quizStats);
        quizStatsRepository.save(quizStats);

        // Create a StudentStats object for the last three course executions (if they exist) and add to the dashboard
        StudentStats studentStats = new StudentStats(courseExecution, teacherDashboard);
        teacherDashboard.addStudentStats(studentStats);
        studentStatsRepository.save(studentStats);

        // Create a QuestionStats object for the last three course executions (if they exist) and add to the dashboard
        QuestionStats questionStats = new QuestionStats(teacherDashboard, courseExecution);
        questionStatsRepository.save(questionStats);

        for (CourseExecution ce : courseExecutions) {
            quizStats = new QuizStats(ce, teacherDashboard);
            studentStats = new StudentStats(ce, teacherDashboard);
            questionStats = new QuestionStats(teacherDashboard, ce);
            teacherDashboard.addQuizStats(quizStats);
            teacherDashboard.addStudentStats(studentStats);
            quizStatsRepository.save(quizStats);
            studentStatsRepository.save(studentStats);
            questionStatsRepository.save(questionStats);
        }
        teacherDashboardRepository.save(teacherDashboard);

        return new TeacherDashboardDto(teacherDashboard);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateTeacherDashboard(int dashboardId) {
        TeacherDashboard teacherDashboard = teacherDashboardRepository.findById(dashboardId).orElseThrow(() -> new TutorException(DASHBOARD_NOT_FOUND, dashboardId));
        teacherDashboard.update();
        teacherDashboardRepository.save(teacherDashboard);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateAllTeacherDashboards() {
        TeacherDashboard teacherDashboard;

        for(Teacher teacher : teacherRepository.findAll()){
            for(CourseExecution execution : teacher.getCourseExecutions()){
                if((teacherDashboard = teacher.getCourseExecutionDashboard(execution)) == null){
                    createTeacherDashboard(execution.getId(), teacher.getId());
                    updateTeacherDashboard(teacher.getCourseExecutionDashboard(execution).getId());
                } else {
                    updateTeacherDashboard(teacherDashboard.getId());
                }
            }
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void removeTeacherDashboard(Integer dashboardId) {
        if (dashboardId == null)
            throw new TutorException(DASHBOARD_NOT_FOUND, -1);

        TeacherDashboard teacherDashboard = teacherDashboardRepository.findById(dashboardId).orElseThrow(() -> new TutorException(DASHBOARD_NOT_FOUND, dashboardId));
        quizStatsRepository.deleteAll(teacherDashboard.getQuizStats());
        studentStatsRepository.deleteAll(teacherDashboard.getStudentStats());
        questionStatsRepository.deleteAll(teacherDashboard.getQuestionStats());
        teacherDashboard.remove();
        teacherDashboardRepository.delete(teacherDashboard);
    }

}
