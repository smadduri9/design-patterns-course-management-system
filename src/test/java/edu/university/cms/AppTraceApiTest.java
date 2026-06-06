package edu.university.cms;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.OfficialPattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AppTraceApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatternTraceService traceService;

    @BeforeEach
    void setUp() {
        traceService.clear();
        traceService.recordPhase2(
                OfficialPattern.BUILDER,
                "AssignmentBuilder",
                "Create assignment",
                "Assignment was assembled step by step",
                "Instructor creates assignment"
        );
        traceService.recordPhase4(
                OfficialPattern.ADAPTER,
                "MockAIServiceAdapter",
                "Run mock AI analysis",
                "Adapted mock AI response for rubric analysis",
                "Mock AI analysis"
        );
        traceService.recordPhase5(
                OfficialPattern.MEMENTO,
                "FeedbackDraft",
                "Edit instructor feedback",
                "Instructor restored feedback draft",
                "Instructor review"
        );
    }

    @Test
    void traceEndpointStillWorksWithNoFilters() throws Exception {
        mockMvc.perform(get("/api/app/trace"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void filtersByCategoryCaseInsensitively() throws Exception {
        mockMvc.perform(get("/api/app/trace").param("category", "structural"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].pattern").value("ADAPTER"));
    }

    @Test
    void filtersByPatternEnumKeyCaseInsensitively() throws Exception {
        mockMvc.perform(get("/api/app/trace").param("pattern", "builder"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].pattern").value("BUILDER"));
    }

    @Test
    void filtersByPatternDisplayNameCaseInsensitively() throws Exception {
        mockMvc.perform(get("/api/app/trace").param("pattern", "Adapter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].patternDisplayName").value("Adapter"));
    }

    @Test
    void filtersByWorkflowStepCaseInsensitively() throws Exception {
        mockMvc.perform(get("/api/app/trace").param("workflowStep", "instructor review"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].pattern").value("MEMENTO"));
    }

    @Test
    void searchesAcrossEventFields() throws Exception {
        mockMvc.perform(get("/api/app/trace").param("search", "rubric"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].className").value("MockAIServiceAdapter"));

        mockMvc.perform(get("/api/app/trace").param("search", "AssignmentBuilder"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].pattern").value("BUILDER"));
    }

    @Test
    void traceEndpointDoesNotCreateClearOrMutateEvents() throws Exception {
        int before = traceService.findAll().size();

        mockMvc.perform(get("/api/app/trace").param("search", "mock"))
                .andExpect(status().isOk());

        assertThat(traceService.findAll()).hasSize(before);
    }

    @Test
    void existingTraceHtmlStillReturnsOk() throws Exception {
        mockMvc.perform(get("/trace"))
                .andExpect(status().isOk());
    }
}
