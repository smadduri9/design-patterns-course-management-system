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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DemoPageTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatternTraceService traceService;

    @Test
    void demoLoadsSuccessfullyWithProjectTitle() throws Exception {
        mockMvc.perform(get("/demo"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("design-patterns-course-management-system")));
    }

    @Test
    void demoIncludesAllOfficialPatternNames() throws Exception {
        String html = mockMvc.perform(get("/demo"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Arrays.stream(OfficialPattern.values())
                .forEach(pattern -> assertThat(html).contains(pattern.getDisplayName()));
    }

    @Test
    void demoIncludesJavaCodeSubmissionAnalysisAndTracePanel() throws Exception {
        mockMvc.perform(get("/demo"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Java code submission analysis")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Mock sandbox/test runner")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("MockAIServiceAdapter")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Design Pattern Trace Panel")));
    }

    @Test
    void demoTraceEventsUseOfficialPatterns() throws Exception {
        mockMvc.perform(get("/demo")).andExpect(status().isOk());

        assertThat(traceService.findAll()).isNotEmpty();
        assertThat(traceService.findAll())
                .allMatch(event -> Arrays.asList(OfficialPattern.values()).contains(event.pattern()));
    }
}
