package edu.university.cms;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.university.cms.domain.User;
import edu.university.cms.domain.UserRole;
import edu.university.cms.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AppCourseApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    void existingDemoAndTraceRoutesStillReturnOk() throws Exception {
        mockMvc.perform(get("/demo")).andExpect(status().isOk());
        mockMvc.perform(get("/trace")).andExpect(status().isOk());
        mockMvc.perform(get("/demo/phase-2")).andExpect(status().isOk());
        mockMvc.perform(get("/demo/phase-3")).andExpect(status().isOk());
        mockMvc.perform(get("/demo/phase-4")).andExpect(status().isOk());
        mockMvc.perform(get("/demo/phase-5")).andExpect(status().isOk());
    }

    @Test
    void instructorCanCreateTwoCoursesAndListThem() throws Exception {
        String firstTitle = uniqueTitle("Design Patterns API");
        String secondTitle = uniqueTitle("Architecture API");

        JsonNode firstCourse = createCourse(firstTitle);
        JsonNode secondCourse = createCourse(secondTitle);

        mockMvc.perform(get("/api/app/courses"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(firstCourse.get("id").asText())))
                .andExpect(content().string(containsString(secondCourse.get("id").asText())))
                .andExpect(content().string(containsString(firstTitle)))
                .andExpect(content().string(containsString(secondTitle)));
    }

    @Test
    void courseDetailReturnsCreatedCourse() throws Exception {
        String title = uniqueTitle("Course Detail");
        JsonNode course = createCourse(title);

        mockMvc.perform(get("/api/app/courses/{courseId}", course.get("id").asText()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(course.get("id").asText()))
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.instructor.role").value("INSTRUCTOR"))
                .andExpect(jsonPath("$.rosterCount").value(0))
                .andExpect(jsonPath("$.assignmentCount").value(0));
    }

    @Test
    void studentsCanBeEnrolledIntoCourse() throws Exception {
        JsonNode course = createCourse(uniqueTitle("Roster Course"));
        List<User> students = students();
        String firstStudentId = students.get(0).getId().toString();
        String secondStudentId = students.get(1).getId().toString();

        mockMvc.perform(post("/api/app/courses/{courseId}/enrollments", course.get("id").asText())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "studentIds": ["%s", "%s"]
                                }
                                """.formatted(firstStudentId, secondStudentId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseId").value(course.get("id").asText()))
                .andExpect(jsonPath("$.students", hasSize(2)));

        mockMvc.perform(get("/api/app/courses/{courseId}/roster", course.get("id").asText()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.students", hasSize(2)))
                .andExpect(content().string(containsString(firstStudentId)))
                .andExpect(content().string(containsString(secondStudentId)));
    }

    @Test
    void duplicateEnrollmentDoesNotDuplicateRosterEntries() throws Exception {
        JsonNode course = createCourse(uniqueTitle("Duplicate Roster"));
        String studentId = students().getFirst().getId().toString();

        mockMvc.perform(post("/api/app/courses/{courseId}/enrollments", course.get("id").asText())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "studentIds": ["%s", "%s"]
                                }
                                """.formatted(studentId, studentId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.students", hasSize(1)));

        mockMvc.perform(post("/api/app/courses/{courseId}/enrollments", course.get("id").asText())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "studentIds": ["%s"]
                                }
                                """.formatted(studentId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.students", hasSize(1)));
    }

    @Test
    void instructorCannotBeEnrolledAsStudent() throws Exception {
        JsonNode course = createCourse(uniqueTitle("Invalid Roster"));
        String instructorId = instructor().getId().toString();

        mockMvc.perform(post("/api/app/courses/{courseId}/enrollments", course.get("id").asText())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "studentIds": ["%s"]
                                }
                                """.formatted(instructorId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unknownCourseOrStudentReturnsControlledClientError() throws Exception {
        String missingCourseId = UUID.randomUUID().toString();
        String validStudentId = students().getFirst().getId().toString();
        JsonNode course = createCourse(uniqueTitle("Unknown Student"));
        String missingStudentId = UUID.randomUUID().toString();

        mockMvc.perform(get("/api/app/courses/{courseId}", missingCourseId))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/app/courses/{courseId}/roster", missingCourseId))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/app/courses/{courseId}/enrollments", missingCourseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "studentIds": ["%s"]
                                }
                                """.formatted(validStudentId)))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/app/courses/{courseId}/enrollments", course.get("id").asText())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "studentIds": ["%s"]
                                }
                                """.formatted(missingStudentId)))
                .andExpect(status().isNotFound());
    }

    private JsonNode createCourse(String title) throws Exception {
        String response = mockMvc.perform(post("/api/app/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "%s"
                                }
                                """.formatted(title)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.instructor.role").value("INSTRUCTOR"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response);
    }

    private List<User> students() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == UserRole.STUDENT)
                .toList();
    }

    private User instructor() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == UserRole.INSTRUCTOR)
                .findFirst()
                .orElseThrow();
    }

    private String uniqueTitle(String prefix) {
        return prefix + " " + UUID.randomUUID();
    }
}
