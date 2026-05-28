package edu.university.cms.application;

import edu.university.cms.domain.OfficialPattern;

import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class PatternTraceService {

    private static final Set<OfficialPattern> PHASE_2_PATTERNS = EnumSet.of(
            OfficialPattern.ABSTRACT_FACTORY,
            OfficialPattern.BUILDER,
            OfficialPattern.COMPOSITE,
            OfficialPattern.COMMAND,
            OfficialPattern.ITERATOR
    );

    private static final Set<OfficialPattern> PHASE_3_PATTERNS = EnumSet.of(
            OfficialPattern.STATE,
            OfficialPattern.CHAIN_OF_RESPONSIBILITY,
            OfficialPattern.MEDIATOR,
            OfficialPattern.FACADE,
            OfficialPattern.FACTORY_METHOD,
            OfficialPattern.TEMPLATE_METHOD
    );

    private static final Set<OfficialPattern> PHASE_4_PATTERNS = EnumSet.of(
            OfficialPattern.ADAPTER,
            OfficialPattern.PROXY,
            OfficialPattern.DECORATOR,
            OfficialPattern.STRATEGY,
            OfficialPattern.FACTORY_METHOD,
            OfficialPattern.TEMPLATE_METHOD
    );

    private static final Set<OfficialPattern> PHASE_5_PATTERNS = EnumSet.of(
            OfficialPattern.MEMENTO,
            OfficialPattern.OBSERVER,
            OfficialPattern.BRIDGE,
            OfficialPattern.STATE
    );

    private final List<PatternTraceEvent> events = new ArrayList<>();

    public synchronized PatternTraceEvent recordPhase2(
            OfficialPattern pattern,
            String className,
            String userAction,
            String description,
            String workflowStep
    ) {
        if (!PHASE_2_PATTERNS.contains(pattern)) {
            throw new IllegalArgumentException("Pattern is not part of the Phase 2 trace set");
        }
        return record(pattern, className, userAction, description, workflowStep);
    }

    public synchronized PatternTraceEvent recordPhase4(
            OfficialPattern pattern,
            String className,
            String userAction,
            String description,
            String workflowStep
    ) {
        if (!PHASE_4_PATTERNS.contains(pattern)) {
            throw new IllegalArgumentException("Pattern is not part of the Phase 4 trace set");
        }
        return record(pattern, className, userAction, description, workflowStep);
    }

    public synchronized PatternTraceEvent recordPhase3(
            OfficialPattern pattern,
            String className,
            String userAction,
            String description,
            String workflowStep
    ) {
        if (!PHASE_3_PATTERNS.contains(pattern)) {
            throw new IllegalArgumentException("Pattern is not part of the Phase 3 trace set");
        }
        return record(pattern, className, userAction, description, workflowStep);
    }

    public synchronized PatternTraceEvent recordPhase5(
            OfficialPattern pattern,
            String className,
            String userAction,
            String description,
            String workflowStep
    ) {
        if (!PHASE_5_PATTERNS.contains(pattern)) {
            throw new IllegalArgumentException("Pattern is not part of the Phase 5 trace set");
        }
        return record(pattern, className, userAction, description, workflowStep);
    }

    public synchronized PatternTraceEvent recordByDisplayName(
            String displayName,
            String className,
            String userAction,
            String description,
            String workflowStep
    ) {
        OfficialPattern pattern = OfficialPattern.fromDisplayName(displayName)
                .orElseThrow(() -> new IllegalArgumentException("Pattern is not in the official allowlist"));
        return record(pattern, className, userAction, description, workflowStep);
    }

    public synchronized List<PatternTraceEvent> findAll() {
        return List.copyOf(events);
    }

    public synchronized void clear() {
        events.clear();
    }

    private PatternTraceEvent record(
            OfficialPattern pattern,
            String className,
            String userAction,
            String description,
            String workflowStep
    ) {
        PatternTraceEvent event = new PatternTraceEvent(
                Instant.now(),
                userAction,
                pattern,
                pattern.getCategory(),
                className,
                description,
                workflowStep
        );
        events.add(event);
        return event;
    }
}
