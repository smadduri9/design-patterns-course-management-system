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

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AppFeedbackApiTest {

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
    void savingFeedbackDraftRecordsDraftHistory() throws Exception {
        JsonNode submission = analyzedSubmission();

        mockMvc.perform(post("/api/app/submissions/{submissionId}/feedback-drafts", submission.get("submissionId").asText())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "feedbackText": "Good use of Adapter. Add one edge-case explanation."
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.submissionId").value(submission.get("submissionId").asText()))
                .andExpect(jsonPath("$.currentFeedback").value("Good use of Adapter. Add one edge-case explanation."))
                .andExpect(jsonPath("$.drafts", hasSize(1)))
                .andExpect(jsonPath("$.drafts[0].index").value(0));
    }

    @Test
    void restoringFeedbackDraftReturnsEarlierFeedbackText() throws Exception {
        JsonNode submission = analyzedSubmission();
        String submissionId = submission.get("submissionId").asText();
        saveDraft(submissionId, "First draft feedback.");
        saveDraft(submissionId, "Second draft feedback.");

        mockMvc.perform(post("/api/app/submissions/{submissionId}/feedback-drafts/restore", submissionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "draftIndex": 0
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentFeedback").value("First draft feedback."))
                .andExpect(jsonPath("$.drafts", hasSize(2)));
    }

    @Test
    void draftEndpointsRequireAnalyzedSubmission() throws Exception {
        JsonNode rawSubmission = submittedOnlySubmission();
        String submissionId = rawSubmission.get("id").asText();

        mockMvc.perform(get("/api/app/submissions/{submissionId}/feedback-drafts", submissionId))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/app/submissions/{submissionId}/feedback-drafts", submissionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "feedbackText": "Draft before analysis"
                                }
                                """))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/app/submissions/{submissionId}/feedback-drafts", UUID.randomUUID().toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void finalizingFeedbackMovesSubmissionToFinalizedAndReturnsGradeAndNotification() throws Exception {
        JsonNode submission = analyzedSubmission();
        String submissionId = submission.get("submissionId").asText();

        mockMvc.perform(post("/api/app/submissions/{submissionId}/final-feedback", submissionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "feedbackText": "Final instructor feedback shown to the student."
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.submissionId").value(submissionId))
                .andExpect(jsonPath("$.status").value("FINALIZED"))
                .andExpect(jsonPath("$.finalFeedback").value("Final instructor feedback shown to the student."))
                .andExpect(jsonPath("$.grade.points").isNumber())
                .andExpect(jsonPath("$.notification.message").value(containsString("Final feedback is available")));

        mockMvc.perform(get("/api/app/submissions/{submissionId}", submissionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FINALIZED"));
    }

    @Test
    void studentFeedbackFailsBeforeFinalizationAndSucceedsAfterFinalization() throws Exception {
        JsonNode submission = analyzedSubmission();
        String submissionId = submission.get("submissionId").asText();

        mockMvc.perform(get("/api/app/submissions/{submissionId}/student-feedback", submissionId))
                .andExpect(status().isBadRequest());

        finalizeFeedback(submissionId, "Student-visible final feedback.");

        mockMvc.perform(get("/api/app/submissions/{submissionId}/student-feedback", submissionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.submissionId").value(submissionId))
                .andExpect(jsonPath("$.finalFeedback").value("Student-visible final feedback."))
                .andExpect(jsonPath("$.grade.points").isNumber())
                .andExpect(jsonPath("$.aiSummary").value(containsString("Mock summary")))
                .andExpect(jsonPath("$.notification.message").value(containsString("Final feedback is available")))
                .andExpect(jsonPath("$.report.summary").value(containsString("Mock summary")));
    }

    @Test
    void unknownSubmissionReturnsControlledNotFound() throws Exception {
        String missingSubmissionId = UUID.randomUUID().toString();

        mockMvc.perform(post("/api/app/submissions/{submissionId}/final-feedback", missingSubmissionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "feedbackText": "Missing submission feedback"
                                }
                                """))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/app/submissions/{submissionId}/student-feedback", missingSubmissionId))
                .andExpect(status().isNotFound());
    }

    private void saveDraft(String submissionId, String text) throws Exception {
        mockMvc.perform(post("/api/app/submissions/{submissionId}/feedback-drafts", submissionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "feedbackText": "%s"
                                }
                                """.formatted(text)))
                .andExpect(status().isOk());
    }

    private void finalizeFeedback(String submissionId, String text) throws Exception {
        mockMvc.perform(post("/api/app/submissions/{submissionId}/final-feedback", submissionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "feedbackText": "%s"
                                }
                                """.formatted(text)))
                .andExpect(status().isOk());
    }

    private JsonNode analyzedSubmission() throws Exception {
        JsonNode rawSubmission = submittedOnlySubmission();
        String response = mockMvc.perform(post("/api/app/submissions/{submissionId}/analyze", rawSubmission.get("id").asText()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response);
    }

    private JsonNode submittedOnlySubmission() throws Exception {
        JsonNode course = createCourse();
        JsonNode assignment = createAssignment(course.get("id").asText());
        String response = mockMvc.perform(post("/api/app/assignments/{assignmentId}/submissions", assignment.get("id").asText())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "studentId": "%s",
                                  "submissionType": "PDF_TEXT",
                                  "content": "This submission explains how Adapter protects domain code."
                                }
                                """.formatted(student().getId())))
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
                                  "title": "Feedback API Course %s"
                                }
                                """.formatted(UUID.randomUUID())))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response);
    }

    private JsonNode createAssignment(String courseId) throws Exception {
        String response = mockMvc.perform(post("/api/app/courses/{courseId}/assignments", courseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Feedback Assignment %s",
                                  "description": "Assignment for feedback API tests.",
                                  "dueDate": "2026-06-15",
                                  "acceptedSubmissionTypes": ["PDF_TEXT"],
                                  "gradingStrategyType": "RUBRIC_WEIGHTED",
                                  "maxPoints": 100,
                                  "rubric": {
                                    "title": "Feedback Rubric",
                                    "criteria": [
                                      {
                                        "name": "Correctness",
                                        "description": "Meets requirements.",
                                        "maxPoints": 50
                                      },
                                      {
                                        "name": "Explanation",
                                        "description": "Explains clearly.",
                                        "maxPoints": 50
                                      }
                                    ]
                                  }
                                }
                                """.formatted(UUID.randomUUID())))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response);
    }

    private User student() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == UserRole.STUDENT)
                .findFirst()
                .orElseThrow();
    }
}
