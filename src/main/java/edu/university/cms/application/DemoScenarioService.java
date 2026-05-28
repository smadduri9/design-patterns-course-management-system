package edu.university.cms.application;

import edu.university.cms.domain.AIAnalysisReport;
import edu.university.cms.domain.Assignment;
import edu.university.cms.domain.Course;
import edu.university.cms.domain.CourseModule;
import edu.university.cms.domain.Grade;
import edu.university.cms.domain.GradingStrategyType;
import edu.university.cms.domain.OfficialPattern;
import edu.university.cms.domain.PatternCategory;
import edu.university.cms.domain.Rubric;
import edu.university.cms.domain.RubricCriterion;
import edu.university.cms.domain.Submission;
import edu.university.cms.domain.SubmissionStatus;
import edu.university.cms.domain.SubmissionType;
import edu.university.cms.domain.TestResult;
import edu.university.cms.domain.User;
import edu.university.cms.domain.UserRole;
import edu.university.cms.patterns.behavioral.command.CommandHistory;
import edu.university.cms.patterns.behavioral.command.CommandInvoker;
import edu.university.cms.patterns.behavioral.command.CreateAssignmentCommand;
import edu.university.cms.patterns.behavioral.command.CreateCourseCommand;
import edu.university.cms.patterns.behavioral.iterator.CourseComponentIterator;
import edu.university.cms.patterns.behavioral.iterator.RubricCriteriaIterator;
import edu.university.cms.patterns.behavioral.strategy.GradingStrategySelector;
import edu.university.cms.patterns.creational.builder.AssignmentBuilder;
import edu.university.cms.patterns.creational.factory.CourseContentFactory;
import edu.university.cms.patterns.creational.factory.ProjectBasedCourseContentFactory;
import edu.university.cms.patterns.structural.adapter.MockAIService;
import edu.university.cms.patterns.structural.adapter.MockAIServiceAdapter;
import edu.university.cms.patterns.structural.adapter.MockCodeSandbox;
import edu.university.cms.patterns.structural.adapter.MockCodeSandboxAdapter;
import edu.university.cms.patterns.structural.composite.CourseComponent;
import edu.university.cms.patterns.structural.composite.CourseComposite;
import edu.university.cms.patterns.structural.composite.ModuleComposite;
import edu.university.cms.patterns.structural.proxy.CachedAnalysisServiceProxy;
import edu.university.cms.patterns.structural.proxy.DefaultAnalysisService;
import edu.university.cms.repository.AssignmentRepository;
import edu.university.cms.repository.CourseRepository;
import edu.university.cms.repository.SubmissionRepository;
import edu.university.cms.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class DemoScenarioService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final SubmissionWorkflowFacade submissionWorkflowFacade;
    private final InstructorReviewFacade instructorReviewFacade;
    private final StudentFeedbackService studentFeedbackService;
    private final PatternTraceService traceService;

    public DemoScenarioService(
            UserRepository userRepository,
            CourseRepository courseRepository,
            AssignmentRepository assignmentRepository,
            SubmissionRepository submissionRepository,
            SubmissionWorkflowFacade submissionWorkflowFacade,
            InstructorReviewFacade instructorReviewFacade,
            StudentFeedbackService studentFeedbackService,
            PatternTraceService traceService
    ) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.submissionWorkflowFacade = submissionWorkflowFacade;
        this.instructorReviewFacade = instructorReviewFacade;
        this.studentFeedbackService = studentFeedbackService;
        this.traceService = traceService;
    }

    public DemoPageView buildDemoPage() {
        traceService.clear();

        ScenarioResultView courseSetup = runCourseSetupDemo();
        SubmissionWorkflowResult textWorkflow = runSubmissionWorkflowDemo(SubmissionType.PDF_TEXT);
        SubmissionWorkflowResult codeWorkflow = runSubmissionWorkflowDemo(SubmissionType.JAVA_CODE);
        AnalysisDemo analysisDemo = runAnalysisDemo();
        InstructorReviewResult reviewResult = instructorReviewFacade.reviewAndFinalize(
                codeWorkflow.submission().getId(),
                "Instructor confirms the mock test results and asks for clearer edge-case handling.",
                "Instructor draft before restore.",
                true
        );
        StudentFeedbackView studentFeedback = studentFeedbackService.viewFeedback(codeWorkflow.submission().getId());

        ScenarioResultView textSubmission = submissionScenario("Text/PDF submission analysis demo", textWorkflow);
        ScenarioResultView codeSubmission = submissionScenario("Java code submission analysis demo", codeWorkflow);
        ScenarioResultView reviewScenario = reviewScenario(reviewResult);
        ScenarioResultView studentScenario = studentScenario(studentFeedback);

        return new DemoPageView(
                "design-patterns-course-management-system",
                List.of("Sriram Madduri", "Rakshitha Srinivasa", "Ankush Rai"),
                "AI-assisted course management system demonstrating 18 object-oriented design patterns.",
                workflowSteps(),
                List.of(courseSetup, textSubmission, codeSubmission, reviewScenario, fullEndToEndScenario(reviewResult)),
                codeSubmissionCard(analysisDemo.codeReport()),
                reportCard("Text/PDF AIAnalysisReport", analysisDemo.textReport()),
                reportCard("Java Code AIAnalysisReport", analysisDemo.codeReport()),
                reviewScenario,
                studentScenario,
                traceViews(),
                patternsByCategory()
        );
    }

    private ScenarioResultView runCourseSetupDemo() {
        User instructor = instructor();
        CourseContentFactory factory = new ProjectBasedCourseContentFactory(traceService);
        CourseModule module = factory.createStarterModule();
        Rubric rubric = factory.createStarterRubric();
        Assignment assignment = factory.createStarterAssignment(rubric);
        Course course = new Course(UUID.randomUUID(), "Design Patterns CS501", instructor, List.of(module));
        CourseComposite courseComposite = new CourseComposite(course, traceService);
        ModuleComposite moduleComposite = new ModuleComposite(module, traceService);
        courseComposite.addModule(moduleComposite);

        CommandInvoker invoker = new CommandInvoker(new CommandHistory(), traceService);
        invoker.execute(new CreateCourseCommand(courseRepository, course));
        invoker.execute(new CreateAssignmentCommand(assignmentRepository, moduleComposite, assignment));

        List<String> hierarchy = new ArrayList<>();
        CourseComponentIterator iterator = new CourseComponentIterator(courseComposite, traceService);
        while (iterator.hasNext()) {
            CourseComponent component = iterator.next();
            hierarchy.add(component.getComponentType() + ": " + component.getTitle());
        }
        RubricCriteriaIterator rubricIterator = new RubricCriteriaIterator(rubric, traceService);
        while (rubricIterator.hasNext()) {
            RubricCriterion criterion = rubricIterator.next();
            hierarchy.add("Rubric criterion: " + criterion.getName());
        }

        return new ScenarioResultView(
                "Course setup demo",
                "Complete",
                "Instructor created a course, module, Java assignment template, and rubric.",
                hierarchy
        );
    }

    private SubmissionWorkflowResult runSubmissionWorkflowDemo(SubmissionType type) {
        User student = student();
        Rubric rubric = demoRubric("Submission Workflow Rubric");
        Assignment assignment = assignmentRepository.save(new AssignmentBuilder()
                .title(type == SubmissionType.JAVA_CODE ? "Java Code Submission Demo" : "Text Submission Demo")
                .description("Unified demo assignment.")
                .dueDate(LocalDate.now().plusDays(7))
                .acceptedSubmissionType(type)
                .rubric(rubric)
                .maxPoints(100)
                .gradingStrategyType(type == SubmissionType.JAVA_CODE ? GradingStrategyType.CODE_TEST : GradingStrategyType.RUBRIC_WEIGHTED)
                .build());
        return submissionWorkflowFacade.submitAssignment(student.getId(), assignment.getId(), type, contentFor(type));
    }

    private AnalysisDemo runAnalysisDemo() {
        User student = student();
        Rubric rubric = demoRubric("AI Analysis Rubric");
        Assignment textAssignment = demoAssignment("Text/PDF AI Analysis", SubmissionType.PDF_TEXT, GradingStrategyType.RUBRIC_WEIGHTED, rubric);
        Assignment codeAssignment = demoAssignment("Java Code AI Analysis", SubmissionType.JAVA_CODE, GradingStrategyType.CODE_TEST, rubric);
        Submission textSubmission = directSubmission(student, textAssignment, SubmissionType.PDF_TEXT, contentFor(SubmissionType.PDF_TEXT));
        Submission codeSubmission = directSubmission(student, codeAssignment, SubmissionType.JAVA_CODE, contentFor(SubmissionType.JAVA_CODE));
        CachedAnalysisServiceProxy analysisService = new CachedAnalysisServiceProxy(
                new DefaultAnalysisService(
                        traceService,
                        new MockAIServiceAdapter(new MockAIService(), traceService),
                        new MockCodeSandboxAdapter(new MockCodeSandbox(), traceService),
                        new GradingStrategySelector()
                ),
                traceService
        );
        AIAnalysisReport textReport = analysisService.analyze(textSubmission, textAssignment);
        AIAnalysisReport codeReport = analysisService.analyze(codeSubmission, codeAssignment);
        analysisService.analyze(codeSubmission, codeAssignment);
        return new AnalysisDemo(textReport, codeReport);
    }

    private ScenarioResultView submissionScenario(String title, SubmissionWorkflowResult result) {
        AIAnalysisReport report = result.analysisReport();
        return new ScenarioResultView(
                title,
                result.submission().getStatus().name(),
                report.getSummary(),
                List.of(
                        "Submission type: " + result.submission().getType().name(),
                        "Rubric findings: " + report.getRubricFindings().size(),
                        "Test results: " + report.getTestResults().size(),
                        "Grade suggestion: " + gradeText(report)
                )
        );
    }

    private ScenarioResultView reportCard(String title, AIAnalysisReport report) {
        return new ScenarioResultView(
                title,
                "Generated",
                report.getSummary(),
                List.of(
                        "Rubric findings: " + report.getRubricFindings().size(),
                        "Test results: " + report.getTestResults().size(),
                        "Suggested feedback: " + report.getSuggestedFeedback(),
                        "Grade suggestion: " + gradeText(report)
                )
        );
    }

    private ScenarioResultView codeSubmissionCard(AIAnalysisReport report) {
        List<String> details = new ArrayList<>();
        details.add("Java code submission only");
        details.add("Mock sandbox/test runner: MockCodeSandboxAdapter");
        details.add("Mock AI explanation: MockAIServiceAdapter");
        for (TestResult result : report.getTestResults()) {
            details.add((result.passed() ? "PASS: " : "NEEDS WORK: ") + result.testName() + " - " + result.output());
        }
        details.add("Rubric findings: " + report.getRubricFindings().size());
        details.add("Suggested feedback: " + report.getSuggestedFeedback());
        details.add("Grade suggestion: " + gradeText(report));
        return new ScenarioResultView(
                "Java code submission analysis",
                "Mock analysis complete",
                report.getSummary(),
                details
        );
    }

    private ScenarioResultView reviewScenario(InstructorReviewResult result) {
        return new ScenarioResultView(
                "Instructor review/final feedback demo",
                result.submission().getStatus().name(),
                "Instructor restored an earlier feedback draft and sent final feedback.",
                List.of(
                        "Final feedback: " + result.finalFeedback(),
                        "Grade: " + result.submission().getGrade().map(grade -> grade.points() + "/" + grade.maxPoints()).orElse("n/a"),
                        "Notification: " + result.notification().getMessage()
                )
        );
    }

    private ScenarioResultView studentScenario(StudentFeedbackView view) {
        return new ScenarioResultView(
                "Student feedback card",
                "Visible to Student",
                view.aiSummary(),
                List.of(
                        "Final feedback: " + view.finalFeedback(),
                        "Grade: " + view.grade().points() + "/" + view.grade().maxPoints(),
                        "Notification: " + view.notification().getMessage()
                )
        );
    }

    private ScenarioResultView fullEndToEndScenario(InstructorReviewResult result) {
        return new ScenarioResultView(
                "Full end-to-end demo",
                "Complete",
                "Course setup, submission, mock analysis, instructor review, and student feedback are demonstrated.",
                List.of(
                        "Final submission status: " + result.submission().getStatus().name(),
                        "Trace events recorded: " + traceService.findAll().size(),
                        "Official pattern count: " + OfficialPattern.values().length
                )
        );
    }

    private Assignment demoAssignment(String title, SubmissionType type, GradingStrategyType strategyType, Rubric rubric) {
        return new AssignmentBuilder()
                .title(title)
                .description("Unified demo analysis assignment.")
                .dueDate(LocalDate.now().plusDays(7))
                .acceptedSubmissionType(type)
                .rubric(rubric)
                .maxPoints(100)
                .gradingStrategyType(strategyType)
                .build();
    }

    private Submission directSubmission(User student, Assignment assignment, SubmissionType type, String content) {
        return new Submission(UUID.randomUUID(), assignment.getId(), student, type, content, Instant.now(), SubmissionStatus.ANALYZING);
    }

    private Rubric demoRubric(String title) {
        return new Rubric(
                UUID.randomUUID(),
                title,
                List.of(
                        new RubricCriterion(UUID.randomUUID(), "Correctness", "Meets assignment requirements.", 50),
                        new RubricCriterion(UUID.randomUUID(), "Explanation", "Explains design decisions clearly.", 50)
                )
        );
    }

    private String contentFor(SubmissionType type) {
        if (type == SubmissionType.JAVA_CODE) {
            return "public class DemoPatternSubmission { public String explain() { return \"mock code analysis\"; } }";
        }
        return "This PDF/text submission explains how design patterns improve the course management workflow.";
    }

    private String gradeText(AIAnalysisReport report) {
        return report.getGradeSuggestion()
                .map(grade -> grade.points() + "/" + grade.maxPoints())
                .orElse("n/a");
    }

    private User instructor() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == UserRole.INSTRUCTOR)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Seeded instructor was not found"));
    }

    private User student() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == UserRole.STUDENT)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Seeded student was not found"));
    }

    private List<DemoWorkflowStepView> workflowSteps() {
        return List.of(
                new DemoWorkflowStepView(1, "Instructor creates course content", "Course, module, assignment, and rubric are assembled."),
                new DemoWorkflowStepView(2, "Student submits work", "PDF/text or Java code content enters the submission workflow."),
                new DemoWorkflowStepView(3, "System validates submission", "Validation handlers check content, timing, and accepted type."),
                new DemoWorkflowStepView(4, "Mock analysis runs", "Mock AI and mock sandbox/test runner produce report data."),
                new DemoWorkflowStepView(5, "AIAnalysisReport is generated", "Summary, rubric findings, test results, feedback, and grade suggestion are produced."),
                new DemoWorkflowStepView(6, "Instructor reviews feedback", "Draft snapshots are saved and restored before final feedback is sent."),
                new DemoWorkflowStepView(7, "Student receives feedback", "Final feedback, grade, notification, and AI summary are visible.")
        );
    }

    private List<DemoTraceView> traceViews() {
        return traceService.findAll().stream()
                .map(event -> new DemoTraceView(
                        event.timestamp().toString(),
                        event.userAction(),
                        event.pattern().getDisplayName(),
                        event.category().name(),
                        event.className(),
                        event.description(),
                        event.workflowStep()
                ))
                .toList();
    }

    private Map<String, List<String>> patternsByCategory() {
        Map<String, List<String>> patterns = new LinkedHashMap<>();
        Arrays.stream(PatternCategory.values()).forEach(category -> patterns.put(
                category.name(),
                Arrays.stream(OfficialPattern.values())
                        .filter(pattern -> pattern.getCategory() == category)
                        .map(OfficialPattern::getDisplayName)
                        .toList()
        ));
        return patterns;
    }

    private record AnalysisDemo(AIAnalysisReport textReport, AIAnalysisReport codeReport) {
    }
}
