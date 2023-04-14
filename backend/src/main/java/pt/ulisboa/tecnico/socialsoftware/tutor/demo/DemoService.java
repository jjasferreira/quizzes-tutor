package pt.ulisboa.tecnico.socialsoftware.tutor.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswerItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.AnswerDetailsRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerItemRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.AuthUserService;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.dto.AuthDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.DifficultQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.repository.StudentDashboardRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.repository.DifficultQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.DiscussionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.CourseExecutionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.TopicConjunction;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.AssessmentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionDetailsRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.repository.QuestionSubmissionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DemoService {
    List<Integer> questions2Keep = Arrays.asList(1320, 1940, 1544, 11081, 11082);

    List<Integer> questionsInQuizzes = Arrays.asList(1940, 11081, 11082);

    Integer quiz2Keep = 40438;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private CourseExecutionService courseExecutionService;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionAnswerItemRepository questionAnswerItemRepository;

    @Autowired
    private DiscussionRepository discussionRepository;

    @Autowired
    private QuestionSubmissionRepository questionSubmissionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentDashboardRepository studentDashboardRepository;

    @Autowired
    private DifficultQuestionRepository difficultQuestionRepository;

    @Autowired
    private AuthUserService authUserService;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private QuestionDetailsRepository questionDetailsRepository;

    @Autowired
    private AnswerDetailsRepository answerDetailsRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoDashboards() {
        userRepository.findAll()
                .stream()
                .filter(user -> user.getAuthUser() != null && user.getAuthUser().isDemoStudent())
                .map(Student.class::cast)
                .flatMap(student -> student.getDashboards().stream())
                .forEach(dashboard -> {
                    dashboard.remove();
                    this.studentDashboardRepository.delete(dashboard);
                });

        Set<DifficultQuestion> difficultQuestionsToRemove = courseExecutionRepository.findById(courseExecutionService.getDemoCourse().getCourseExecutionId()).stream()
                .flatMap(courseExecution -> courseExecution.getDifficultQuestions().stream())
                .collect(Collectors.toSet());

        difficultQuestionsToRemove.forEach(difficultQuestion -> {
            difficultQuestion.remove();
            difficultQuestionRepository.delete(difficultQuestion);
        });
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoAssessments() {
        Integer courseExecutionId = courseExecutionService.getDemoCourse().getCourseExecutionId();

        this.assessmentRepository.findByExecutionCourseId(courseExecutionId)
                .stream()
                .forEach(assessment -> {
                    assessment.remove();
                    assessmentRepository.delete(assessment);
                });
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoTopics() {
        Integer courseId = courseExecutionService.getDemoCourse().getCourseId();

        this.topicRepository.findTopics(courseId)
                .stream()
                .forEach(topic -> {
                    topic.remove();
                    this.topicRepository.delete(topic);
                });
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoDiscussions() {
        List<Discussion> discussions = discussionRepository.findByExecutionCourseId(courseExecutionService.getDemoCourse().getCourseExecutionId());

        discussions.forEach(discussion -> {
            discussion.remove();
            discussionRepository.delete(discussion);
        });
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoAnswers() {
        Set<QuestionAnswerItem> questionAnswerItems = questionAnswerItemRepository.findDemoStudentQuestionAnswerItems();
        questionAnswerItemRepository.deleteAll(questionAnswerItems);

        Set<QuizAnswer> quizAnswers = quizAnswerRepository.findByExecutionCourseId(courseExecutionService.getDemoCourse().getCourseExecutionId());

        for (QuizAnswer quizAnswer : quizAnswers) {
            if (!quizAnswer.getQuiz().getId().equals(quiz2Keep) || !quizAnswer.getStudent().getUsername().equals(DemoUtils.STUDENT_USERNAME)) {
                quizAnswer.remove();
                quizAnswerRepository.delete(quizAnswer);
            }
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoTournaments() {
        tournamentRepository.getTournamentsForCourseExecution(courseExecutionService.getDemoCourse().getCourseExecutionId())
                .forEach(tournament -> {
                    tournament.getParticipants().forEach(user -> user.removeTournament(tournament));
                    if (tournament.getQuiz() != null) {
                        tournament.getQuiz().setTournament(null);
                    }

                    tournamentRepository.delete(tournament);
                });
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoQuizzes() {
        // remove quizzes except to keep
        quizRepository.findQuizzesOfExecution(courseExecutionService.getDemoCourse().getCourseExecutionId())
                .stream()
                .forEach(quiz -> {
                    if (!quiz.getId().equals(quiz2Keep)) {
                        quiz.remove();
                        this.quizRepository.delete(quiz);
                    }
                });

        // remove questions except to keep and that are not submitted
        for (Question question : questionRepository.findQuestions(courseExecutionService.getDemoCourse().getCourseId())
                .stream()
                .filter(question -> !questions2Keep.contains(question.getId()) && questionSubmissionRepository.findQuestionSubmissionByQuestionId(question.getId()) == null)
                .collect(Collectors.toList())) {
            questionService.removeQuestion(question.getId());
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoQuestionSubmissions() {
        questionSubmissionRepository.findQuestionSubmissionsByCourseExecution(courseExecutionService.getDemoCourse().getCourseExecutionId())
                .forEach(questionSubmission -> {
                    questionSubmission.remove();
                    questionSubmissionRepository.delete(questionSubmission);
                });
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetDemoStudents() {
        userRepository.findAll()
                .stream()
                .filter(user -> user.getAuthUser() != null && user.getAuthUser().isDemoStudent())
                .map(Student.class::cast)
                .forEach(student -> {
                    if (student.getQuizAnswers().isEmpty()) {
                        student.remove();
                        this.userRepository.delete(student);
                    }
                });
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void populateDemo() {
        Integer courseId = courseExecutionService.getDemoCourse().getCourseId();
        Integer courseExecutionId = courseExecutionService.getDemoCourse().getCourseExecutionId();

        Topic softwareArchitectureTopic = new Topic();
        softwareArchitectureTopic.setName("Software Architecture");
        softwareArchitectureTopic.setCourse(courseRepository.findById(courseId).orElseThrow(() -> new TutorException(ErrorMessage.COURSE_NOT_FOUND)));
        topicRepository.save(softwareArchitectureTopic);

        Topic softwareEngineeringTopic = new Topic();
        softwareEngineeringTopic.setName("Software Engineering");
        softwareEngineeringTopic.setCourse(courseRepository.findById(courseId).orElseThrow(() -> new TutorException(ErrorMessage.COURSE_NOT_FOUND)));
        topicRepository.save(softwareEngineeringTopic);

        List<Question> questions = questionRepository.findQuestions(courseId);
        questions.forEach(question -> {
            question.setStatus(Question.Status.AVAILABLE);
            question.addTopic(softwareEngineeringTopic);
        });

        Assessment assessment = new Assessment();
        assessment.setTitle("Software Engineering Questions");
        assessment.setStatus(Assessment.Status.AVAILABLE);
        assessment.setSequence(1);
        assessment.setCourseExecution(courseExecutionRepository.findById(courseExecutionId).orElseThrow(() -> new TutorException(ErrorMessage.COURSE_EXECUTION_NOT_FOUND)));
        TopicConjunction topicConjunction = new TopicConjunction();
        topicConjunction.addTopic(softwareEngineeringTopic);
        topicConjunction.setAssessment(assessment);
        assessmentRepository.save(assessment);

        Quiz inClassOneWayQuiz = new Quiz();
        inClassOneWayQuiz.setTitle("In Class Quiz One Way");
        inClassOneWayQuiz.setType(Quiz.QuizType.IN_CLASS.name());
        inClassOneWayQuiz.setCreationDate(DateHandler.now());
        inClassOneWayQuiz.setAvailableDate(DateHandler.now());
        inClassOneWayQuiz.setConclusionDate(DateHandler.now().plusHours(22));
        inClassOneWayQuiz.setCourseExecution(courseExecutionRepository.findById(courseExecutionId).orElseThrow(() -> new TutorException(ErrorMessage.COURSE_EXECUTION_NOT_FOUND)));
        inClassOneWayQuiz.setOneWay(true);
        inClassOneWayQuiz.setScramble(true);

        Quiz inClassQuiz = new Quiz();
        inClassQuiz.setTitle("In Class Quiz");
        inClassQuiz.setType(Quiz.QuizType.IN_CLASS.name());
        inClassQuiz.setCreationDate(DateHandler.now());
        inClassQuiz.setAvailableDate(DateHandler.now());
        inClassQuiz.setConclusionDate(DateHandler.now().plusHours(22));
        inClassQuiz.setCourseExecution(courseExecutionRepository.findById(courseExecutionId).orElseThrow(() -> new TutorException(ErrorMessage.COURSE_EXECUTION_NOT_FOUND)));
        inClassQuiz.setScramble(true);

        Quiz proposedQuiz = new Quiz();
        proposedQuiz.setTitle("Teacher Proposed");
        proposedQuiz.setType(Quiz.QuizType.PROPOSED.name());
        proposedQuiz.setCreationDate(DateHandler.now());
        proposedQuiz.setAvailableDate(DateHandler.now());
        proposedQuiz.setCourseExecution(courseExecutionRepository.findById(courseExecutionId).orElseThrow(() -> new TutorException(ErrorMessage.COURSE_EXECUTION_NOT_FOUND)));
        proposedQuiz.setScramble(true);

        Quiz scrambledQuiz = new Quiz();
        scrambledQuiz.setTitle("Non Scrambled");
        scrambledQuiz.setType(Quiz.QuizType.PROPOSED.name());
        scrambledQuiz.setCreationDate(DateHandler.now());
        scrambledQuiz.setAvailableDate(DateHandler.now());
        scrambledQuiz.setCourseExecution(courseExecutionRepository.findById(courseExecutionId).orElseThrow(() -> new TutorException(ErrorMessage.COURSE_EXECUTION_NOT_FOUND)));

        questions.forEach(question -> {
            if (questionsInQuizzes.contains(question.getId())) {
                new QuizQuestion(inClassOneWayQuiz, question, inClassOneWayQuiz.getQuizQuestionsNumber());
                new QuizQuestion(inClassQuiz, question, inClassQuiz.getQuizQuestionsNumber());
                new QuizQuestion(proposedQuiz, question, proposedQuiz.getQuizQuestionsNumber());
                new QuizQuestion(scrambledQuiz, question, scrambledQuiz.getQuizQuestionsNumber());
            }
        });

        quizRepository.save(inClassOneWayQuiz);
        quizRepository.save(inClassQuiz);
        quizRepository.save(proposedQuiz);
        quizRepository.save(scrambledQuiz);

        // Simulate login of demo teacher (this adds the demo teacher to the original demo execution)
        AuthDto authDemoTeacherDto = authUserService.demoTeacherAuth();

        // Get demo course and demo teacher
        Course newCourse = createCourse("New Course");
        User demoTeacher = userRepository.findById(authDemoTeacherDto.getUser().getId())
                .orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND));

        // Create 3 course executions for the demo course
        // 2019
        CourseExecution courseExecution19 = createCourseExecution(newCourse, "2019NewCourse", "1 Semestre 2019/2020", DateHandler.toLocalDateTime("2019-12-31"));
        // Add demo teacher and students to course execution
        addUserToCourseExecution(courseExecution19, demoTeacher);
        Student student119 = createStudent("Student 1 19/20", "s119", "s119@ist.pt");
        Student student219 = createStudent("Student 2 19/20", "s219", "s219@ist.pt");
        addUserToCourseExecution(courseExecution19, student119);
        addUserToCourseExecution(courseExecution19, student219);
        /* Add quizzes to course execution
            * Quiz 1 solved by students 1 and 2
            * Quiz 2 solved by student 1
            * Quiz 3 solved by student 1
            * Quiz 4 solved by student 1
            * Quiz 5 solved by noone */
        /* Expected results
            * NumQuizzes = 5
            * UniqueQuizzesSolved = 4
            * AverageQuizzesSolved = (4 + 1) / 2 = 2.5 */
        Quiz quiz119 = createQuiz(courseExecution19, "Quiz 1 2019");
        QuizQuestion quizQuestion119 = createQuizQuestion(newCourse, quiz119, 0);
        QuizAnswer quizAnswer1191 = createQuizAnswer(quiz119, student119);
        QuizAnswer quizAnswer1192 = createQuizAnswer(quiz119, student219);

        Quiz quiz219 = createQuiz(courseExecution19, "Quiz 2 2019");
        QuizQuestion quizQuestion219 = createQuizQuestion(newCourse, quiz219, 0);
        QuizAnswer quizAnswer2192 = createQuizAnswer(quiz219, student119);

        Quiz quiz319 = createQuiz(courseExecution19, "Quiz 3 2019");
        QuizQuestion quizQuestion319 = createQuizQuestion(newCourse, quiz319, 0);
        QuizAnswer quizAnswer3192 = createQuizAnswer(quiz319, student119);

        Quiz quiz419 = createQuiz(courseExecution19, "Quiz 4 2019");
        QuizQuestion quizQuestion419 = createQuizQuestion(newCourse, quiz419, 0);
        QuizAnswer quizAnswer4191 = createQuizAnswer(quiz419, student119);

        Quiz quiz519 = createQuiz(courseExecution19, "Quiz 5 2019");

        // 2022
        CourseExecution courseExecution22 = createCourseExecution(newCourse, "2022NewCourse", "1 Semestre 2022/2023", DateHandler.toLocalDateTime("2022-12-31"));
        // Add demo teacher and students to course execution
        addUserToCourseExecution(courseExecution22, demoTeacher);
        Student student122 = createStudent("Student 1 22/23", "s122", "s122@ist.pt");
        Student student222 = createStudent("Student 2 22/23", "s222", "s222@ist.pt");
        Student student322 = createStudent("Student 3 22/23", "s322", "s322@ist.pt");
        addUserToCourseExecution(courseExecution22, student122);
        addUserToCourseExecution(courseExecution22, student222);
        addUserToCourseExecution(courseExecution22, student322);
        /* Add quizzes to course execution
            * Quiz 1 solved by students 1, 2 and 3
            * Quiz 2 solved by students 1 and 3
            * Quiz 3 solved by noone
            * Quiz 4 solved by students 1 and 2 */
        /* Expected results
            * NumQuizzes = 4
            * UniqueQuizzesSolved = 3
            * AverageQuizzesSolved = (3 + 2 + 2) / 3 = 2.33 */
        Quiz quiz122 = createQuiz(courseExecution22, "Quiz 1 2022");
        QuizQuestion quizQuestion122 = createQuizQuestion(newCourse, quiz122, 0);
        QuizAnswer quizAnswer1221 = createQuizAnswer(quiz122, student122);
        QuizAnswer quizAnswer1222 = createQuizAnswer(quiz122, student222);
        QuizAnswer quizAnswer1223 = createQuizAnswer(quiz122, student322);

        Quiz quiz222 = createQuiz(courseExecution22, "Quiz 2 2022");
        QuizQuestion quizQuestion222 = createQuizQuestion(newCourse, quiz222, 0);
        QuizAnswer quizAnswer2222 = createQuizAnswer(quiz222, student222);
        QuizAnswer quizAnswer2223 = createQuizAnswer(quiz222, student322);

        Quiz quiz322 = createQuiz(courseExecution22, "Quiz 3 2022");

        Quiz quiz422 = createQuiz(courseExecution22, "Quiz 4 2022");
        QuizQuestion quizQuestion422 = createQuizQuestion(newCourse, quiz422, 0);
        QuizAnswer quizAnswer4221 = createQuizAnswer(quiz422, student122);
        QuizAnswer quizAnswer4222 = createQuizAnswer(quiz422, student222);

        // 2023
        CourseExecution courseExecution23 = createCourseExecution(newCourse, "2023NewCourse", "1 Semestre 2023/2024", DateHandler.toLocalDateTime("2023-12-31"));
        // Add demo teacher and students to course execution
        addUserToCourseExecution(courseExecution23, demoTeacher);
        Student student123 = createStudent("Student 1 23/24", "s123", "s123@ist.pt");
        Student student223 = createStudent("Student 2 23/24", "s223", "s223@ist.pt");
        addUserToCourseExecution(courseExecution23, student123);
        addUserToCourseExecution(courseExecution23, student223);
        /* Add quizzes to course execution
            * Quiz 1 solved by student 1
            * Quiz 2 solved by student 2
            * Quiz 3 solved by noone */
        /* Expected results
            * NumQuizzes = 3
            * UniqueQuizzesSolved = 2
            * AverageQuizzesSolved = (1 + 1) / 2 = 1 */
        Quiz quiz123 = createQuiz(courseExecution23, "Quiz 1 2023");
        QuizQuestion quizQuestion123 = createQuizQuestion(newCourse, quiz123, 0);
        QuizAnswer quizAnswer123 = createQuizAnswer(quiz123, student122);

        Quiz quiz223 = createQuiz(courseExecution23, "Quiz 2 2023");
        QuizQuestion quizQuestion223 = createQuizQuestion(newCourse, quiz223, 0);
        QuizAnswer quizAnswer223 = createQuizAnswer(quiz223, student222);

        Quiz quiz323 = createQuiz(courseExecution23, "Quiz 3 2023");
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Course createCourse(String name) {
        Course course = new Course(name, Course.Type.TECNICO);
        courseRepository.save(course);
        return course;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    private CourseExecution createCourseExecution(Course course, String acronym, String academicTerm, LocalDateTime endDate) {
        CourseExecution courseExecution = new CourseExecution(course, acronym, academicTerm, Course.Type.TECNICO, endDate);
        courseExecutionRepository.save(courseExecution);
        courseRepository.save(course);
        return courseExecution;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Student createStudent(String name, String username, String email) {
        Student student = new Student(name, username, email, false, AuthUser.Type.TECNICO);
        userRepository.save(student);
        return student;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void addUserToCourseExecution(CourseExecution courseExecution, User user) {
        user.addCourse(courseExecution);
        userRepository.save(user);
        courseExecutionRepository.save(courseExecution);
    }

    public Quiz createQuiz(CourseExecution courseExecution, String title) {
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setCourseExecution(courseExecution);
        quiz.setType(Quiz.QuizType.PROPOSED.toString());
        quiz.setCreationDate(DateHandler.now());
        quiz.setAvailableDate(DateHandler.now());
        quizRepository.save(quiz);
        courseExecutionRepository.save(courseExecution);
        return quiz;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public QuestionAnswer createQuestionAnswer(QuizAnswer quizAnswer, QuizQuestion quizQuestion, boolean correct) {
        QuestionAnswer questionAnswer = new QuestionAnswer();
        questionAnswer.setTimeTaken(99);
        questionAnswer.setQuizAnswer(quizAnswer);
        questionAnswer.setQuizQuestion(quizQuestion);
        Option option;
        if (correct) {
            MultipleChoiceQuestion questionDetails = (MultipleChoiceQuestion) quizQuestion.getQuestion().getQuestionDetails();
            option = questionDetails.getOptions().get(0);
        } else {
            MultipleChoiceQuestion questionDetails = (MultipleChoiceQuestion) quizQuestion.getQuestion().getQuestionDetails();
            option = questionDetails.getOptions().get(1);
        }
        MultipleChoiceAnswer answerDetails = new MultipleChoiceAnswer(questionAnswer, option);
        questionAnswer.setAnswerDetails(answerDetails);
        questionAnswerRepository.save(questionAnswer);
        answerDetailsRepository.save(answerDetails);
        quizAnswerRepository.save(quizAnswer);
        quizQuestionRepository.save(quizQuestion);
        return questionAnswer;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Question createQuestion(Course course, String title) {
        Question newQuestion = new Question();
        newQuestion.setCourse(course);
        newQuestion.setTitle(title);
        MultipleChoiceQuestion questionDetails = new MultipleChoiceQuestion();
        newQuestion.setQuestionDetails(questionDetails);
        Option optionA = new Option();
        optionA.setContent("A");
        optionA.setCorrect(true);
        optionA.setSequence(0);
        optionA.setQuestionDetails(questionDetails);
        Option optionB = new Option();
        optionB.setContent("B");
        optionB.setCorrect(false);
        optionB.setSequence(1);
        optionB.setQuestionDetails(questionDetails);
        questionDetailsRepository.save(questionDetails);
        questionRepository.save(newQuestion);
        optionRepository.save(optionA);
        optionRepository.save(optionB);
        courseRepository.save(course);
        return newQuestion;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public QuizAnswer createQuizAnswer(Quiz quiz, Student student) {
        QuizAnswer quizAnswer = new QuizAnswer();
        LocalDateTime now = DateHandler.now();
        quizAnswer.setCompleted(true);
        quizAnswer.setCreationDate(now);
        quizAnswer.setAnswerDate(now);
        quizAnswer.setQuiz(quiz);
        quizAnswer.setStudent(student);
        quizAnswerRepository.save(quizAnswer);
        quizRepository.save(quiz);
        userRepository.save(student);
        return quizAnswer;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public QuizQuestion createQuizQuestion(Course course, Quiz quiz, int seq) {
        Question question = createQuestion(course, "Question Title");
        QuizQuestion quizQuestion = new QuizQuestion(quiz, question, seq);
        quizQuestionRepository.save(quizQuestion);
        quizRepository.save(quiz);
        return quizQuestion;
    }

}
