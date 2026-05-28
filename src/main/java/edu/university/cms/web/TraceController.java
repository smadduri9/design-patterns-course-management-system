package edu.university.cms.web;

import edu.university.cms.application.PatternTraceEvent;
import edu.university.cms.application.PatternTraceService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TraceController {

    private final PatternTraceService traceService;

    public TraceController(PatternTraceService traceService) {
        this.traceService = traceService;
    }

    @GetMapping(value = "/trace", produces = MediaType.TEXT_HTML_VALUE)
    public String showTrace(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String workflowStep
    ) {
        List<PatternTraceEvent> events = traceService.findAll().stream()
                .filter(event -> category == null || event.category().name().equalsIgnoreCase(category))
                .filter(event -> workflowStep == null || event.workflowStep().equalsIgnoreCase(workflowStep))
                .toList();

        StringBuilder html = new StringBuilder();
        html.append("<!doctype html><html><head><title>Design Pattern Trace Panel</title>");
        html.append("<style>body{font-family:Arial,sans-serif;margin:2rem;}table{border-collapse:collapse;width:100%;}");
        html.append("th,td{border:1px solid #ddd;padding:.5rem;text-align:left;}th{background:#f4f4f4;}</style>");
        html.append("</head><body><h1>Design Pattern Trace Panel</h1>");
        html.append("<p>Only official backend trace events are shown. No sample or hardcoded frontend trace data is displayed.</p>");
        html.append("<table><thead><tr><th>Timestamp</th><th>User Action</th><th>Pattern</th><th>Category</th>");
        html.append("<th>Class</th><th>Description</th><th>Workflow Step</th></tr></thead><tbody>");
        for (PatternTraceEvent event : events) {
            html.append("<tr>")
                    .append("<td>").append(escape(event.timestamp().toString())).append("</td>")
                    .append("<td>").append(escape(event.userAction())).append("</td>")
                    .append("<td>").append(escape(event.pattern().getDisplayName())).append("</td>")
                    .append("<td>").append(escape(event.category().name())).append("</td>")
                    .append("<td>").append(escape(event.className())).append("</td>")
                    .append("<td>").append(escape(event.description())).append("</td>")
                    .append("<td>").append(escape(event.workflowStep())).append("</td>")
                    .append("</tr>");
        }
        html.append("</tbody></table></body></html>");
        return html.toString();
    }

    private String escape(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
