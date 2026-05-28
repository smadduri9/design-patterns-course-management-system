package edu.university.cms.web;

import edu.university.cms.application.DemoPageView;
import edu.university.cms.application.DemoScenarioService;
import edu.university.cms.application.DemoTraceView;
import edu.university.cms.application.DemoWorkflowStepView;
import edu.university.cms.application.ScenarioResultView;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class LandingController {

    private final DemoScenarioService demoScenarioService;

    public LandingController(DemoScenarioService demoScenarioService) {
        this.demoScenarioService = demoScenarioService;
    }

    @GetMapping(value = {"/", "/demo"}, produces = MediaType.TEXT_HTML_VALUE)
    public String showLandingPage() {
        DemoPageView view = demoScenarioService.buildDemoPage();
        StringBuilder html = new StringBuilder();
        html.append("<!doctype html><html><head><title>").append(escape(view.projectTitle())).append("</title>");
        html.append("<style>");
        html.append(":root{--bg:#f8f9fb;--card:#fff;--text:#1a202c;--muted:#6b7280;--border:#e5e7eb;--primary:#4f46e5;--accent:#8b5cf6;--green:#10b981;--blue:#3b82f6;--purple:#8b5cf6;}");
        html.append("*{box-sizing:border-box}body{margin:0;background:var(--bg);color:var(--text);font-family:Inter,Arial,sans-serif;line-height:1.5;}");
        html.append("a{color:var(--primary);text-decoration:none}.layout{display:grid;grid-template-columns:minmax(0,1fr) 380px;gap:24px;max-width:1480px;margin:0 auto;padding:28px;}");
        html.append(".hero{background:linear-gradient(135deg,#4f46e5,#8b5cf6);color:white;border-radius:20px;padding:30px;box-shadow:0 16px 40px rgba(79,70,229,.18);}");
        html.append(".hero h1{margin:0 0 10px;font-size:32px}.hero p{margin:0;opacity:.94}.team{display:flex;gap:10px;flex-wrap:wrap;margin-top:18px}.pill{border-radius:999px;padding:6px 12px;background:rgba(255,255,255,.16);font-size:13px;}");
        html.append(".section{margin-top:24px}.section h2{font-size:20px;margin:0 0 14px}.grid{display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:16px}.workflow{display:grid;grid-template-columns:repeat(7,minmax(0,1fr));gap:10px;}");
        html.append(".card{background:var(--card);border:1px solid var(--border);border-radius:16px;padding:18px;box-shadow:0 8px 24px rgba(15,23,42,.04);}.card h3{margin:0 0 8px;font-size:16px}.card p{margin:0;color:var(--muted);font-size:14px}");
        html.append(".status{display:inline-block;margin-bottom:10px;border-radius:999px;background:#eef2ff;color:var(--primary);font-size:12px;font-weight:700;padding:4px 9px}.details{margin:12px 0 0;padding-left:18px;color:#374151;font-size:13px}");
        html.append(".step{background:white;border:1px solid var(--border);border-radius:14px;padding:12px}.step-num{width:28px;height:28px;border-radius:50%;background:var(--primary);color:white;display:flex;align-items:center;justify-content:center;font-weight:700;margin-bottom:8px}.step h3{font-size:13px;margin:0 0 5px}.step p{font-size:12px;color:var(--muted);margin:0}");
        html.append(".trace{position:sticky;top:20px;align-self:start;max-height:calc(100vh - 40px);overflow:auto}.trace-item{border:1px solid var(--border);border-radius:12px;padding:12px;margin-bottom:10px;background:white}.trace-title{display:flex;justify-content:space-between;gap:8px;font-size:13px;font-weight:700}.trace-meta{font-size:11px;color:var(--muted);margin-top:4px}.trace-desc{font-size:12px;color:#374151;margin-top:6px}.badge{border-radius:999px;padding:2px 7px;font-size:10px;color:white}.CREATIONAL{background:var(--green)}.STRUCTURAL{background:var(--blue)}.BEHAVIORAL{background:var(--purple)}");
        html.append(".patterns{display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:16px}.pattern-list{display:flex;flex-wrap:wrap;gap:8px}.pattern-chip{border:1px solid var(--border);border-radius:999px;background:#fafafa;padding:6px 10px;font-size:12px}.links{display:flex;flex-wrap:wrap;gap:10px}.link-card{background:white;border:1px solid var(--border);border-radius:12px;padding:10px 12px;font-size:13px}");
        html.append("@media(max-width:1100px){.layout{grid-template-columns:1fr}.trace{position:static;max-height:none}.workflow{grid-template-columns:repeat(2,minmax(0,1fr))}.patterns,.grid{grid-template-columns:1fr}}");
        html.append("</style></head><body>");
        html.append("<main class=\"layout\"><div>");
        appendHero(html, view);
        appendWorkflow(html, view.workflowSteps());
        appendScenarioCards(html, view.scenarioResults());
        appendReportSection(html, view);
        appendPatternMap(html, view.patternsByCategory());
        appendBackupLinks(html);
        html.append("</div>");
        appendTracePanel(html, view.traceEvents());
        html.append("</main></body></html>");
        return html.toString();
    }

    private void appendHero(StringBuilder html, DemoPageView view) {
        html.append("<section class=\"hero\"><h1>").append(escape(view.projectTitle())).append("</h1>");
        html.append("<p>").append(escape(view.overview())).append("</p><div class=\"team\">");
        for (String member : view.teamMembers()) {
            html.append("<span class=\"pill\">").append(escape(member)).append("</span>");
        }
        html.append("</div></section>");
    }

    private void appendWorkflow(StringBuilder html, List<DemoWorkflowStepView> steps) {
        html.append("<section class=\"section\"><h2>Main Workflow</h2><div class=\"workflow\">");
        for (DemoWorkflowStepView step : steps) {
            html.append("<article class=\"step\"><div class=\"step-num\">").append(step.number()).append("</div>");
            html.append("<h3>").append(escape(step.title())).append("</h3>");
            html.append("<p>").append(escape(step.description())).append("</p></article>");
        }
        html.append("</div></section>");
    }

    private void appendScenarioCards(StringBuilder html, List<ScenarioResultView> scenarios) {
        html.append("<section class=\"section\"><h2>Scenario Results</h2><div class=\"grid\">");
        for (ScenarioResultView scenario : scenarios) {
            appendScenarioCard(html, scenario);
        }
        html.append("</div></section>");
    }

    private void appendReportSection(StringBuilder html, DemoPageView view) {
        html.append("<section class=\"section\"><h2>AIAnalysisReport Highlights</h2><div class=\"grid\">");
        appendScenarioCard(html, view.textReportResult());
        appendScenarioCard(html, view.codeReportResult());
        appendScenarioCard(html, view.codeSubmissionResult());
        appendScenarioCard(html, view.instructorReviewResult());
        appendScenarioCard(html, view.studentFeedbackResult());
        html.append("</div></section>");
    }

    private void appendScenarioCard(StringBuilder html, ScenarioResultView scenario) {
        html.append("<article class=\"card\"><span class=\"status\">").append(escape(scenario.status())).append("</span>");
        html.append("<h3>").append(escape(scenario.title())).append("</h3>");
        html.append("<p>").append(escape(scenario.summary())).append("</p>");
        html.append("<ul class=\"details\">");
        for (String detail : scenario.details()) {
            html.append("<li>").append(escape(detail)).append("</li>");
        }
        html.append("</ul></article>");
    }

    private void appendPatternMap(StringBuilder html, Map<String, List<String>> patternsByCategory) {
        html.append("<section class=\"section\"><h2>Official 18 Design Patterns</h2><div class=\"patterns\">");
        for (Map.Entry<String, List<String>> entry : patternsByCategory.entrySet()) {
            html.append("<article class=\"card\"><h3>").append(escape(entry.getKey())).append("</h3><div class=\"pattern-list\">");
            for (String pattern : entry.getValue()) {
                html.append("<span class=\"pattern-chip\">").append(escape(pattern)).append("</span>");
            }
            html.append("</div></article>");
        }
        html.append("</div></section>");
    }

    private void appendBackupLinks(StringBuilder html) {
        html.append("<section class=\"section\"><h2>Backup Demo Routes</h2><div class=\"links\">");
        html.append("<a class=\"link-card\" href=\"/demo/phase-2\">Course setup JSON</a>");
        html.append("<a class=\"link-card\" href=\"/demo/phase-3\">Submission workflow JSON</a>");
        html.append("<a class=\"link-card\" href=\"/demo/phase-4\">Mock analysis JSON</a>");
        html.append("<a class=\"link-card\" href=\"/demo/phase-5\">Review JSON</a>");
        html.append("<a class=\"link-card\" href=\"/trace\">Full trace table</a>");
        html.append("</div></section>");
    }

    private void appendTracePanel(StringBuilder html, List<DemoTraceView> traces) {
        html.append("<aside class=\"trace\"><section class=\"card\"><h2>Design Pattern Trace Panel</h2>");
        html.append("<p>Only official backend trace events are shown.</p></section>");
        for (DemoTraceView trace : traces) {
            html.append("<article class=\"trace-item\"><div class=\"trace-title\"><span>")
                    .append(escape(trace.pattern()))
                    .append("</span><span class=\"badge ")
                    .append(escape(trace.category()))
                    .append("\">")
                    .append(escape(trace.category()))
                    .append("</span></div>");
            html.append("<div class=\"trace-meta\">").append(escape(trace.timestamp())).append(" · ")
                    .append(escape(trace.className())).append("</div>");
            html.append("<div class=\"trace-desc\"><strong>").append(escape(trace.userAction())).append("</strong><br>")
                    .append(escape(trace.description())).append("<br><em>")
                    .append(escape(trace.workflowStep())).append("</em></div></article>");
        }
        html.append("</aside>");
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
