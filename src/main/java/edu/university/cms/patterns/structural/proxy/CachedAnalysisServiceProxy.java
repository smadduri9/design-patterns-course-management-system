package edu.university.cms.patterns.structural.proxy;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.AIAnalysisReport;
import edu.university.cms.domain.Assignment;
import edu.university.cms.domain.OfficialPattern;
import edu.university.cms.domain.Submission;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CachedAnalysisServiceProxy implements AnalysisService {

    private final AnalysisService delegate;
    private final PatternTraceService traceService;
    private final Map<UUID, AIAnalysisReport> cache = new ConcurrentHashMap<>();

    public CachedAnalysisServiceProxy(AnalysisService delegate, PatternTraceService traceService) {
        this.delegate = delegate;
        this.traceService = traceService;
    }

    @Override
    public AIAnalysisReport analyze(Submission submission, Assignment assignment) {
        trace("Checked analysis cache for submission " + submission.getId());
        AIAnalysisReport cached = cache.get(submission.getId());
        if (cached != null) {
            trace("Returned cached analysis report for submission " + submission.getId());
            return cached;
        }
        AIAnalysisReport report = delegate.analyze(submission, assignment);
        cache.put(submission.getId(), report);
        trace("Stored analysis report in cache for submission " + submission.getId());
        return report;
    }

    private void trace(String description) {
        if (traceService != null) {
            traceService.recordPhase4(
                    OfficialPattern.PROXY,
                    getClass().getSimpleName(),
                    "Analyze submission through cache",
                    description,
                    "AIAnalysisReport caching"
            );
        }
    }
}
