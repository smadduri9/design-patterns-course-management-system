package edu.university.cms;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.OfficialPattern;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AppReadApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatternTraceService traceService;

    @Test
    void existingStableRoutesStillReturnOk() throws Exception {
        mockMvc.perform(get("/demo")).andExpect(status().isOk());
        mockMvc.perform(get("/trace")).andExpect(status().isOk());
        mockMvc.perform(get("/demo/phase-2")).andExpect(status().isOk());
        mockMvc.perform(get("/demo/phase-3")).andExpect(status().isOk());
        mockMvc.perform(get("/demo/phase-4")).andExpect(status().isOk());
        mockMvc.perform(get("/demo/phase-5")).andExpect(status().isOk());
    }

    @Test
    void officialPatternCatalogStaysAtExactlyEighteenWithoutSingleton() {
        assertThat(OfficialPattern.values()).hasSize(18);
        assertThat(Arrays.stream(OfficialPattern.values()).map(OfficialPattern::name))
                .doesNotContain("SINGLETON");
        assertThat(Arrays.stream(OfficialPattern.values()).map(OfficialPattern::getDisplayName))
                .doesNotContain("Singleton");
    }

    @Test
    void dashboardReturnsInstructorAndReadOnlyCounts() throws Exception {
        mockMvc.perform(get("/api/app/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.instructor.role").value("INSTRUCTOR"))
                .andExpect(jsonPath("$.counts.courses").isNumber())
                .andExpect(jsonPath("$.counts.students").value(greaterThanOrEqualTo(5)))
                .andExpect(jsonPath("$.counts.assignments").isNumber())
                .andExpect(jsonPath("$.counts.submissions").isNumber())
                .andExpect(jsonPath("$.counts.traceEvents").isNumber());
    }

    @Test
    void instructorEndpointReturnsSingleSeededInstructor() throws Exception {
        mockMvc.perform(get("/api/app/instructor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sriram Madduri"))
                .andExpect(jsonPath("$.role").value("INSTRUCTOR"));
    }

    @Test
    void studentsEndpointReturnsAtLeastFiveSeededStudents() throws Exception {
        mockMvc.perform(get("/api/app/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(5))))
                .andExpect(jsonPath("$[0].role").value("STUDENT"));
    }

    @Test
    void patternsEndpointReturnsExactlyOfficialPatternsWithoutSingleton() throws Exception {
        mockMvc.perform(get("/api/app/patterns"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(18)))
                .andExpect(content().string(not(containsString("Singleton"))));
    }

    @Test
    void traceEndpointReturnsPatternTraceServiceEventsOnly() throws Exception {
        traceService.clear();
        traceService.recordPhase2(
                OfficialPattern.BUILDER,
                "AppReadApiTest",
                "Verify trace API",
                "Test event recorded directly in PatternTraceService",
                "Read-only trace API"
        );

        mockMvc.perform(get("/api/app/trace"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].pattern").value("BUILDER"))
                .andExpect(jsonPath("$[0].patternDisplayName").value("Builder"))
                .andExpect(jsonPath("$[0].className").value("AppReadApiTest"));
    }
}
