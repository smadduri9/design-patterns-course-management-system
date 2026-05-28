package edu.university.cms;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.Assignment;
import edu.university.cms.domain.Course;
import edu.university.cms.domain.CourseModule;
import edu.university.cms.domain.OfficialPattern;
import edu.university.cms.domain.Rubric;
import edu.university.cms.domain.RubricCriterion;
import edu.university.cms.domain.SubmissionType;
import edu.university.cms.domain.User;
import edu.university.cms.domain.UserRole;
import edu.university.cms.patterns.behavioral.command.CommandHistory;
import edu.university.cms.patterns.behavioral.command.CommandInvoker;
import edu.university.cms.patterns.behavioral.command.CreateAssignmentCommand;
import edu.university.cms.patterns.behavioral.command.CreateCourseCommand;
import edu.university.cms.patterns.behavioral.iterator.CourseComponentIterator;
import edu.university.cms.patterns.behavioral.iterator.RubricCriteriaIterator;
import edu.university.cms.patterns.creational.builder.AssignmentBuilder;
import edu.university.cms.patterns.creational.factory.ProjectBasedCourseContentFactory;
import edu.university.cms.patterns.creational.factory.StandardCourseContentFactory;
import edu.university.cms.patterns.structural.composite.AssignmentLeaf;
import edu.university.cms.patterns.structural.composite.CourseComponent;
import edu.university.cms.patterns.structural.composite.CourseComposite;
import edu.university.cms.patterns.structural.composite.ModuleComposite;
import edu.university.cms.repository.AssignmentRepository;
import edu.university.cms.repository.CourseRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Phase2PatternsTest {

    @Test
    void assignmentBuilderBuildsValidAssignment() {
        Rubric rubric = rubric("Builder Rubric");

        Assignment assignment = new AssignmentBuilder()
                .title("Builder Assignment")
                .description("Create an assignment step by step.")
                .dueDate(LocalDate.now().plusDays(7))
                .acceptedSubmissionType(SubmissionType.PDF_TEXT)
                .rubric(rubric)
                .maxPoints(100)
                .build();

        assertThat(assignment.getTitle()).isEqualTo("Builder Assignment");
        assertThat(assignment.getAcceptedSubmissionTypes()).containsExactly(SubmissionType.PDF_TEXT);
        assertThat(assignment.getRubric()).isSameAs(rubric);
        assertThat(assignment.getMaxPoints()).isEqualTo(100);
    }

    @Test
    void assignmentBuilderRejectsMissingRequiredFields() {
        assertThatThrownBy(() -> new AssignmentBuilder()
                .description("Missing title.")
                .acceptedSubmissionType(SubmissionType.PDF_TEXT)
                .rubric(rubric("Missing Field Rubric"))
                .maxPoints(100)
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("title is required");
    }

    @Test
    void abstractFactoriesCreateCompatibleTemplates() {
        StandardCourseContentFactory standardFactory = new StandardCourseContentFactory();
        Rubric standardRubric = standardFactory.createStarterRubric();
        Assignment standardAssignment = standardFactory.createStarterAssignment(standardRubric);

        ProjectBasedCourseContentFactory projectFactory = new ProjectBasedCourseContentFactory();
        Rubric projectRubric = projectFactory.createStarterRubric();
        Assignment projectAssignment = projectFactory.createStarterAssignment(projectRubric);

        assertThat(standardFactory.createStarterModule().getTitle()).contains("Pattern Foundations");
        assertThat(standardAssignment.getAcceptedSubmissionTypes()).containsExactly(SubmissionType.PDF_TEXT);
        assertThat(standardAssignment.getRubric()).isSameAs(standardRubric);

        assertThat(projectFactory.createStarterModule().getTitle()).contains("Course Project");
        assertThat(projectAssignment.getAcceptedSubmissionTypes()).containsExactly(SubmissionType.JAVA_CODE);
        assertThat(projectAssignment.getRubric()).isSameAs(projectRubric);
    }

    @Test
    void compositeHierarchyContainsCourseModuleAssignment() {
        CourseComposite course = courseComposite();
        ModuleComposite module = moduleComposite();
        AssignmentLeaf assignment = new AssignmentLeaf(assignment("Composite Assignment"));

        module.addAssignment(assignment);
        course.addModule(module);

        assertThat(course.getChildren()).containsExactly(module);
        assertThat(course.getChildren().getFirst().getChildren()).containsExactly(assignment);
    }

    @Test
    void courseComponentIteratorTraversesInExpectedOrder() {
        CourseComposite course = courseComposite();
        ModuleComposite module = moduleComposite();
        AssignmentLeaf assignment = new AssignmentLeaf(assignment("Iterator Assignment"));
        module.addAssignment(assignment);
        course.addModule(module);

        CourseComponentIterator iterator = new CourseComponentIterator(course);
        List<String> titles = new ArrayList<>();
        while (iterator.hasNext()) {
            CourseComponent component = iterator.next();
            titles.add(component.getTitle());
        }

        assertThat(titles).containsExactly(
                "Design Patterns CS501",
                "Module 1",
                "Iterator Assignment"
        );
    }

    @Test
    void rubricCriteriaIteratorTraversesCriteriaInExpectedOrder() {
        Rubric rubric = new Rubric(
                UUID.randomUUID(),
                "Iterator Rubric",
                List.of(
                        new RubricCriterion(UUID.randomUUID(), "Design", "Shows design quality.", 50),
                        new RubricCriterion(UUID.randomUUID(), "Clarity", "Explains decisions clearly.", 50)
                )
        );

        RubricCriteriaIterator iterator = new RubricCriteriaIterator(rubric);
        List<String> names = new ArrayList<>();
        while (iterator.hasNext()) {
            names.add(iterator.next().getName());
        }

        assertThat(names).containsExactly("Design", "Clarity");
    }

    @Test
    void commandInvokerExecutesCreateCourseAndCreateAssignmentCommands() {
        CourseRepository courseRepository = new CourseRepository();
        AssignmentRepository assignmentRepository = new AssignmentRepository();
        PatternTraceService traceService = new PatternTraceService();
        CommandInvoker invoker = new CommandInvoker(new CommandHistory(), traceService);
        CourseComposite courseComposite = courseComposite();
        ModuleComposite moduleComposite = moduleComposite();
        Assignment assignment = assignment("Command Assignment");

        Course savedCourse = invoker.execute(new CreateCourseCommand(courseRepository, courseComposite.getCourse()));
        Assignment savedAssignment = invoker.execute(new CreateAssignmentCommand(
                assignmentRepository,
                moduleComposite,
                assignment
        ));

        assertThat(courseRepository.findById(savedCourse.getId())).contains(savedCourse);
        assertThat(assignmentRepository.findById(savedAssignment.getId())).contains(savedAssignment);
        assertThat(moduleComposite.getChildren()).hasSize(1);
        assertThat(invoker.getHistory().findAll())
                .extracting(CommandHistory.CommandHistoryEntry::commandName)
                .containsExactly("Create course", "Create assignment");
        assertThat(traceService.findAll())
                .extracting(event -> event.pattern().getDisplayName())
                .contains("Command");
    }

    @Test
    void patternTraceServiceRejectsUnsupportedPatternNames() {
        PatternTraceService traceService = new PatternTraceService();

        assertThatThrownBy(() -> traceService.recordByDisplayName(
                "Unsupported Pattern",
                "TestClass",
                "Test action",
                "Test description",
                "Test workflow"
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void patternTraceServiceRestrictsPhase2TraceSet() {
        PatternTraceService traceService = new PatternTraceService();

        assertThatThrownBy(() -> traceService.recordPhase2(
                OfficialPattern.ADAPTER,
                "TestClass",
                "Test action",
                "Test description",
                "Test workflow"
        )).isInstanceOf(IllegalArgumentException.class);
    }

    private static CourseComposite courseComposite() {
        User instructor = new User(UUID.randomUUID(), "Sriram Madduri", UserRole.INSTRUCTOR);
        Course course = new Course(UUID.randomUUID(), "Design Patterns CS501", instructor, List.of());
        return new CourseComposite(course);
    }

    private static ModuleComposite moduleComposite() {
        return new ModuleComposite(new CourseModule(UUID.randomUUID(), "Module 1", List.of()));
    }

    private static Assignment assignment(String title) {
        return new AssignmentBuilder()
                .title(title)
                .description("Phase 2 test assignment.")
                .acceptedSubmissionType(SubmissionType.PDF_TEXT)
                .rubric(rubric("Test Rubric"))
                .maxPoints(100)
                .build();
    }

    private static Rubric rubric(String title) {
        return new Rubric(
                UUID.randomUUID(),
                title,
                List.of(new RubricCriterion(
                        UUID.randomUUID(),
                        "Correctness",
                        "Meets the assignment requirements.",
                        100
                ))
        );
    }
}
