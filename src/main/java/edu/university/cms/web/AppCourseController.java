package edu.university.cms.web;

import edu.university.cms.application.CourseAppService;
import edu.university.cms.application.CourseDetailResponse;
import edu.university.cms.application.CourseResponse;
import edu.university.cms.application.CreateCourseRequest;
import edu.university.cms.application.EnrollmentRequest;
import edu.university.cms.application.RosterAppService;
import edu.university.cms.application.RosterResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/app/courses")
public class AppCourseController {

    private final CourseAppService courseAppService;
    private final RosterAppService rosterAppService;

    public AppCourseController(CourseAppService courseAppService, RosterAppService rosterAppService) {
        this.courseAppService = courseAppService;
        this.rosterAppService = rosterAppService;
    }

    @GetMapping
    public List<CourseResponse> courses() {
        return courseAppService.listCourses();
    }

    @PostMapping
    public CourseResponse createCourse(@RequestBody CreateCourseRequest request) {
        return courseAppService.createCourse(request);
    }

    @GetMapping("/{courseId}")
    public CourseDetailResponse courseDetail(@PathVariable UUID courseId) {
        return courseAppService.courseDetail(courseId);
    }

    @GetMapping("/{courseId}/roster")
    public RosterResponse roster(@PathVariable UUID courseId) {
        return rosterAppService.roster(courseId);
    }

    @PostMapping("/{courseId}/enrollments")
    public RosterResponse enroll(@PathVariable UUID courseId, @RequestBody EnrollmentRequest request) {
        return rosterAppService.enroll(courseId, request);
    }
}
