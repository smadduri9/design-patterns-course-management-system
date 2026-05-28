package edu.university.cms.patterns.creational.factory;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.patterns.behavioral.strategy.GradingStrategy;
import edu.university.cms.patterns.behavioral.template.SubmissionAnalyzer;
import edu.university.cms.patterns.structural.adapter.AIClient;
import edu.university.cms.patterns.structural.adapter.SandboxRunner;

public interface SubmissionAnalyzerFactory {

    SubmissionAnalyzer createAnalyzer(
            PatternTraceService traceService,
            AIClient aiClient,
            SandboxRunner sandboxRunner,
            GradingStrategy gradingStrategy
    );
}
