package edu.university.cms;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AppAssignmentApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    void assignmentCreationPersistsAssignmentAndRubric() throws Exception {
        JsonNode course = createCourse("Assignments Course");

        JsonNode assignment = createTextAssignment(course.get("id").asText(), "Adapter Pattern Essay");

        mockMvc.perform(get("/api/app/assignments/{assignmentId}", assignment.get("id").asText()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(assignment.get("id").asText()))
                .andExpect(jsonPath("$.courseId").value(course.get("id").asText()))
                .andExpect(jsonPath("$.title").value("Adapter Pattern Essay"))
                .andExpect(jsonPath("$.acceptedSubmissionTypes[0]").value("PDF_TEXT"))
                .andExpect(jsonPath("$.gradingStrategyType").value("RUBRIC_WEIGHTED"))
                .andExpect(jsonPath("$.rubric.title").value("Essay Rubric"))
                .andExpect(jsonPath("$.rubric.criteria", hasSize(2)))
                .andExpect(jsonPath("$.rubric.criteria[0].name").value("Correctness"));
    }

    @Test
    void courseAssignmentListReturnsOnlyAssignmentsLinkedToThatCourse() throws Exception {
        JsonNode firstCourse = createCourse("First Assignment List");
        JsonNode secondCourse = createCourse("Second Assignment List");
        JsonNode firstAssignment = createTextAssignment(firstCourse.get("id").asText(), "First Course Essay");
        JsonNode secondAssignment = createTextAssignment(secondCourse.get("id").asText(), "Second Course Essay");

        mockMvc.perform(get("/api/app/courses/{courseId}/assignments", firstCourse.get("id").asText()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(firstAssignment.get("id").asText())))
                .andExpect(content().string(not(containsString(secondAssignment.get("id").asText()))))
                .andExpect(content().string(containsString("First Course Essay")))
                .andExpect(content().string(not(containsString("Second Course Essay"))));
    }

    @Test
    void courseDetailReflectsLinkedAssignmentCount() throws Exception {
        JsonNode course = createCourse("Assignment Count Course");
        createTextAssignment(course.get("id").asText(), "Counted Essay");

        mockMvc.perform(get("/api/app/courses/{courseId}", course.get("id").asText()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignmentCount").value(1));
    }

    @Test
    void javaCodeAssignmentCanBeCreatedUsingExistingEnums() throws Exception {
        JsonNode course = createCourse("Java Assignment Course");

        String response = mockMvc.perform(post("/api/app/courses/{courseId}/assignments", course.get("id").asText())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Java Code Patterns",
                                  "description": "Submit Java code that demonstrates a pattern.",
                                  "dueDate": "2026-06-20",
                                  "acceptedSubmissionTypes": ["JAVA_CODE"],
                                  "gradingStrategyType": "CODE_TEST",
                                  "maxPoints": 100,
                                  "rubric": {
                                    "title": "Code Rubric",
                                    "criteria": [
                                      {
                                        "name": "Compiles",
                                        "description": "Code is suitable for mock sandbox checks.",
                                        "maxPoints": 100
                                      }
                                    ]
                                  }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.acceptedSubmissionTypes[0]").value("JAVA_CODE"))
                .andExpect(jsonPath("$.gradingStrategyType").value("CODE_TEST"))
                .andExpect(jsonPath("$.rubric.criteria[0].name").value("Compiles"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode assignment = objectMapper.readTree(response);
        mockMvc.perform(get("/api/app/assignments/{assignmentId}", assignment.get("id").asText()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Java Code Patterns"));
    }

    @Test
    void unknownCourseAndAssignmentReturnControlledNotFound() throws Exception {
        JsonNode course = createCourse("Known Course");

        mockMvc.perform(get("/api/app/courses/{courseId}/assignments", UUID.randomUUID().toString()))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/app/courses/{courseId}/assignments", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(textAssignmentJson("Missing Course Essay")))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/app/assignments/{assignmentId}", UUID.randomUUID().toString()))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/app/courses/{courseId}/assignments", course.get("id").asText()))
                .andExpect(status().isOk());
    }

    @Test
    void emptyRubricCriteriaReturnsControlledBadRequest() throws Exception {
        JsonNode course = createCourse("Invalid Rubric Course");

        mockMvc.perform(post("/api/app/courses/{courseId}/assignments", course.get("id").asText())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Invalid Rubric Assignment",
                                  "description": "This should fail validation.",
                                  "dueDate": "2026-06-15",
                                  "acceptedSubmissionTypes": ["PDF_TEXT"],
                                  "gradingStrategyType": "RUBRIC_WEIGHTED",
                                  "maxPoints": 100,
                                  "rubric": {
                                    "title": "Empty Rubric",
                                    "criteria": []
                                  }
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    private JsonNode createCourse(String titlePrefix) throws Exception {
        String title = titlePrefix + " " + UUID.randomUUID();
        String response = mockMvc.perform(post("/api/app/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "%s"
                                }
                                """.formatted(title)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response);
    }

    private JsonNode createTextAssignment(String courseId, String title) throws Exception {
        String response = mockMvc.perform(post("/api/app/courses/{courseId}/assignments", courseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(textAssignmentJson(title)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(title))
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response);
    }

    private String textAssignmentJson(String title) {
        return """
                {
                  "title": "%s",
                  "description": "Explain how Adapter protects the domain from external services.",
                  "dueDate": "2026-06-15",
                  "acceptedSubmissionTypes": ["PDF_TEXT"],
                  "gradingStrategyType": "RUBRIC_WEIGHTED",
                  "maxPoints": 100,
                  "rubric": {
                    "title": "Essay Rubric",
                    "criteria": [
                      {
                        "name": "Correctness",
                        "description": "Accurate use of pattern concepts.",
                        "maxPoints": 50
                      },
                      {
                        "name": "Explanation",
                        "description": "Clear reasoning and examples.",
                        "maxPoints": 50
                      }
                    ]
                  }
                }
                """.formatted(title);
    }
}
