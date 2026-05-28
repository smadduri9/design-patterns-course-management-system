package edu.university.cms.web;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.application.TraceEventResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/app")
public class AppTraceController {

    private final PatternTraceService traceService;

    public AppTraceController(PatternTraceService traceService) {
        this.traceService = traceService;
    }

    @GetMapping("/trace")
    public List<TraceEventResponse> trace(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String pattern,
            @RequestParam(required = false) String workflowStep,
            @RequestParam(required = false) String search
    ) {
        return traceService.findAll().stream()
                .filter(event -> matches(category, event.category().name()))
                .filter(event -> pattern == null || pattern.isBlank()
                        || matches(pattern, event.pattern().name())
                        || matches(pattern, event.pattern().getDisplayName()))
                .filter(event -> matches(workflowStep, event.workflowStep()))
                .filter(event -> matchesSearch(search, event.userAction(), event.pattern().getDisplayName(),
                        event.className(), event.description(), event.workflowStep()))
                .map(TraceEventResponse::from)
                .toList();
    }

    private boolean matches(String filter, String value) {
        return filter == null || filter.isBlank() || normalize(value).equals(normalize(filter));
    }

    private boolean matchesSearch(String search, String... values) {
        if (search == null || search.isBlank()) {
            return true;
        }
        String normalizedSearch = normalize(search);
        for (String value : values) {
            if (normalize(value).contains(normalizedSearch)) {
                return true;
            }
        }
        return false;
    }

    private String normalize(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }
}
