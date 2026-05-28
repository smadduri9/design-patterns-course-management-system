package edu.university.cms.web;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.application.TraceEventResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/app")
public class AppTraceController {

    private final PatternTraceService traceService;

    public AppTraceController(PatternTraceService traceService) {
        this.traceService = traceService;
    }

    @GetMapping("/trace")
    public List<TraceEventResponse> trace() {
        return traceService.findAll().stream()
                .map(TraceEventResponse::from)
                .toList();
    }
}
