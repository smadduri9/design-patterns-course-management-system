package edu.university.cms;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.university.cms.application.PatternTraceService;
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
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AppSubmissionApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatternTraceService traceService;

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
    void creatingSubmissionDoesNotCreateAnalysisReport() throws Exception {
        JsonNode assignment = createTextAssignment();
        JsonNode submission = createSubmission(
                assignment.get("id").asText(),
                student().getId().toString(),
                "PDF_TEXT",
                "This text submission explains how Adapter keeps mock services outside the domain."
        );

        mockMvc.perform(get("/api/app/submissions/{submissionId}", submission.get("id").asText()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUBMITTED"))
                .andExpect(jsonPath("$.hasAnalysisReport").value(false))
                .andExpect(jsonPath("$.report").doesNotExist());
    }

    @Test
    void assignmentSubmissionsListReturnsOnlySubmissionsForThatAssignment() throws Exception {
        JsonNode firstAssignment = createTextAssignment();
        JsonNode secondAssignment = createTextAssignment();
        JsonNode firstSubmission = createSubmission(
                firstAssignment.get("id").asText(),
                student().getId().toString(),
                "PDF_TEXT",
                "This first submission belongs to the first assignment."
        );
        JsonNode secondSubmission = createSubmission(
                secondAssignment.get("id").asText(),
                student().getId().toString(),
                "PDF_TEXT",
                "This second submission belongs to the second assignment."
        );

        mockMvc.perform(get("/api/app/assignments/{assignmentId}/submissions", firstAssignment.get("id").asText()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(firstSubmission.get("id").asText())))
                .andExpect(content().string(not(containsString(secondSubmission.get("id").asText()))));
    }

    @Test
    void textSubmissionAnalysisMovesToAwaitingReviewAndStoresReport() throws Exception {
        JsonNode assignment = createTextAssignment();
        JsonNode submission = createSubmission(
                assignment.get("id").asText(),
                student().getId().toString(),
                "PDF_TEXT",
                "This text submission explains how Adapter protects the domain from external services."
        );

        mockMvc.perform(post("/api/app/submissions/{submissionId}/analyze", submission.get("id").asText()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.submissionId").value(submission.get("id").asText()))
                .andExpect(jsonPath("$.status").value("AWAITING_REVIEW"))
                .andExpect(jsonPath("$.report.summary").value(containsString("Mock summary")))
                .andExpect(jsonPath("$.report.rubricFindings", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.report.testResults", empty()))
                .andExpect(jsonPath("$.report.suggestedFeedback").isString())
                .andExpect(jsonPath("$.report.gradeSuggestion.points").isNumber());

        mockMvc.perform(get("/api/app/submissions/{submissionId}", submission.get("id").asText()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AWAITING_REVIEW"))
                .andExpect(jsonPath("$.hasAnalysisReport").value(true));
    }

    @Test
    void javaSubmissionAnalysisIncludesMockSandboxTestResults() throws Exception {
        JsonNode assignment = createJavaAssignment();
        JsonNode submission = createSubmission(
                assignment.get("id").asText(),
                student().getId().toString(),
                "JAVA_CODE",
                "public class Demo { public String explain() { return \"Adapter\"; } }"
        );

        mockMvc.perform(post("/api/app/submissions/{submissionId}/analyze", submission.get("id").asText()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AWAITING_REVIEW"))
                .andExpect(jsonPath("$.report.testResults", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.report.gradeSuggestion.points").isNumber());
    }

    @Test
    void reanalyzingAlreadyAnalyzedSubmissionReturnsExistingReportWithoutDuplicateEvents() throws Exception {
        JsonNode assignment = createTextAssignment();
        JsonNode submission = createSubmission(
                assignment.get("id").asText(),
                student().getId().toString(),
                "PDF_TEXT",
                "This text submission is deterministic for repeated analysis."
        );

        traceService.clear();
        String firstResponse = mockMvc.perform(post("/api/app/submissions/{submissionId}/analyze", submission.get("id").asText()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        int traceCountAfterFirstAnalysis = traceService.findAll().size();
        JsonNode firstAnalysis = objectMapper.readTree(firstResponse);

        String secondResponse = mockMvc.perform(post("/api/app/submissions/{submissionId}/analyze", submission.get("id").asText()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode secondAnalysis = objectMapper.readTree(secondResponse);

        org.assertj.core.api.Assertions.assertThat(secondAnalysis.get("report").get("id").asText())
                .isEqualTo(firstAnalysis.get("report").get("id").asText());
        org.assertj.core.api.Assertions.assertThat(traceService.findAll()).hasSize(traceCountAfterFirstAnalysis);
    }

    @Test
    void unknownAssignmentSubmissionAndInvalidStudentReturnControlledClientErrors() throws Exception {
        JsonNode assignment = createTextAssignment();

        mockMvc.perform(get("/api/app/assignments/{assignmentId}/submissions", UUID.randomUUID().toString()))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/app/assignments/{assignmentId}/submissions", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(submissionJson(student().getId().toString(), "PDF_TEXT", "Valid prose submission.")))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/app/assignments/{assignmentId}/submissions", assignment.get("id").asText())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(submissionJson(UUID.randomUUID().toString(), "PDF_TEXT", "Valid prose submission.")))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/app/submissions/{submissionId}", UUID.randomUUID().toString()))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/app/submissions/{submissionId}/analyze", UUID.randomUUID().toString()))
                .andExpect(status().isNotFound());
    }

    private JsonNode createSubmission(String assignmentId, String studentId, String type, String content) throws Exception {
        String response = mockMvc.perform(post("/api/app/assignments/{assignmentId}/submissions", assignmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(submissionJson(studentId, type, content)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignmentId").value(assignmentId))
                .andExpect(jsonPath("$.student.id").value(studentId))
                .andExpect(jsonPath("$.type").value(type))
                .andExpect(jsonPath("$.status").value("SUBMITTED"))
                .andExpect(jsonPath("$.hasAnalysisReport").value(false))
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response);
    }

    private JsonNode createTextAssignment() throws Exception {
        return createAssignment("PDF_TEXT", "RUBRIC_WEIGHTED");
    }

    private JsonNode createJavaAssignment() throws Exception {
        return createAssignment("JAVA_CODE", "CODE_TEST");
    }

    private JsonNode createAssignment(String submissionType, String strategyType) throws Exception {
        JsonNode course = createCourse();
        String response = mockMvc.perform(post("/api/app/courses/{courseId}/assignments", course.get("id").asText())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Submission Assignment %s",
                                  "description": "Assignment for submission API tests.",
                                  "dueDate": "2026-06-15",
                                  "acceptedSubmissionTypes": ["%s"],
                                  "gradingStrategyType": "%s",
                                  "maxPoints": 100,
                                  "rubric": {
                                    "title": "Submission Rubric",
                                    "criteria": [
                                      {
                                        "name": "Correctness",
                                        "description": "Meets assignment requirements.",
                                        "maxPoints": 50
                                      },
                                      {
                                        "name": "Explanation",
                                        "description": "Explains design decisions clearly.",
                                        "maxPoints": 50
                                      }
                                    ]
                                  }
                                }
                                """.formatted(UUID.randomUUID(), submissionType, strategyType)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response);
    }

    private JsonNode createCourse() throws Exception {
        String response = mockMvc.perform(post("/api/app/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Submission API Course %s"
                                }
                                """.formatted(UUID.randomUUID())))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response);
    }

    private User student() {
        List<User> students = userRepository.findAll().stream()
                .filter(user -> user.getRole() == UserRole.STUDENT)
                .toList();
        return students.getFirst();
    }

    private String submissionJson(String studentId, String type, String content) {
        return """
                {
                  "studentId": "%s",
                  "submissionType": "%s",
                  "content": "%s"
                }
                """.formatted(studentId, type, content.replace("\"", "\\\""));
    }
}
