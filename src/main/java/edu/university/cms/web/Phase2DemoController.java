package edu.university.cms.web;

import edu.university.cms.application.PatternTraceEvent;
import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.Assignment;
import edu.university.cms.domain.Course;
import edu.university.cms.domain.CourseModule;
import edu.university.cms.domain.Rubric;
import edu.university.cms.domain.RubricCriterion;
import edu.university.cms.domain.User;
import edu.university.cms.domain.UserRole;
import edu.university.cms.patterns.behavioral.command.CommandHistory;
import edu.university.cms.patterns.behavioral.command.CommandInvoker;
import edu.university.cms.patterns.behavioral.command.CreateAssignmentCommand;
import edu.university.cms.patterns.behavioral.command.CreateCourseCommand;
import edu.university.cms.patterns.behavioral.iterator.CourseComponentIterator;
import edu.university.cms.patterns.behavioral.iterator.RubricCriteriaIterator;
import edu.university.cms.patterns.creational.factory.CourseContentFactory;
import edu.university.cms.patterns.creational.factory.ProjectBasedCourseContentFactory;
import edu.university.cms.patterns.structural.composite.CourseComponent;
import edu.university.cms.patterns.structural.composite.CourseComposite;
import edu.university.cms.patterns.structural.composite.ModuleComposite;
import edu.university.cms.repository.AssignmentRepository;
import edu.university.cms.repository.CourseRepository;
import edu.university.cms.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class Phase2DemoController {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final AssignmentRepository assignmentRepository;
    private final PatternTraceService traceService;

    public Phase2DemoController(
            UserRepository userRepository,
            CourseRepository courseRepository,
            AssignmentRepository assignmentRepository,
            PatternTraceService traceService
    ) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.assignmentRepository = assignmentRepository;
        this.traceService = traceService;
    }

    @GetMapping("/demo/phase-2")
    public Phase2DemoResponse showPhase2Demo() {
        traceService.clear();

        User instructor = userRepository.findAll().stream()
                .filter(user -> user.getRole() == UserRole.INSTRUCTOR)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Seeded instructor was not found"));

        CourseContentFactory factory = new ProjectBasedCourseContentFactory(traceService);
        CourseModule module = factory.createStarterModule();
        Rubric rubric = factory.createStarterRubric();
        Assignment assignment = factory.createStarterAssignment(rubric);

        Course course = new Course(
                UUID.randomUUID(),
                "Design Patterns CS501",
                instructor,
                List.of(module)
        );

        CourseComposite courseComposite = new CourseComposite(course, traceService);
        ModuleComposite moduleComposite = new ModuleComposite(module, traceService);
        courseComposite.addModule(moduleComposite);

        CommandInvoker invoker = new CommandInvoker(new CommandHistory(), traceService);
        invoker.execute(new CreateCourseCommand(courseRepository, course));
        invoker.execute(new CreateAssignmentCommand(assignmentRepository, moduleComposite, assignment));

        List<String> hierarchy = new ArrayList<>();
        CourseComponentIterator courseIterator = new CourseComponentIterator(courseComposite, traceService);
        while (courseIterator.hasNext()) {
            CourseComponent component = courseIterator.next();
            hierarchy.add(component.getComponentType() + ": " + component.getTitle());
        }

        List<String> rubricCriteria = new ArrayList<>();
        RubricCriteriaIterator rubricIterator = new RubricCriteriaIterator(rubric, traceService);
        while (rubricIterator.hasNext()) {
            RubricCriterion criterion = rubricIterator.next();
            rubricCriteria.add(criterion.getName() + " (" + criterion.getMaxPoints() + " pts)");
        }

        return new Phase2DemoResponse(
                instructor.getName(),
                course.getTitle(),
                module.getTitle(),
                assignment.getTitle(),
                rubric.getTitle(),
                hierarchy,
                rubricCriteria,
                traceService.findAll()
        );
    }

    public record Phase2DemoResponse(
            String instructor,
            String course,
            String module,
            String assignment,
            String rubric,
            List<String> courseHierarchy,
            List<String> rubricCriteria,
            List<PatternTraceEvent> patternTraceEvents
    ) {
    }
}
