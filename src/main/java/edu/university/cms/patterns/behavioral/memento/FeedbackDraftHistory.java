package edu.university.cms.patterns.behavioral.memento;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.OfficialPattern;

import java.util.ArrayList;
import java.util.List;

public class FeedbackDraftHistory {

    private final PatternTraceService traceService;
    private final List<FeedbackDraftMemento> snapshots = new ArrayList<>();

    public FeedbackDraftHistory(PatternTraceService traceService) {
        this.traceService = traceService;
    }

    public void save(FeedbackDraft draft) {
        snapshots.add(draft.save());
        trace("Feedback draft history stored a memento snapshot");
    }

    public FeedbackDraftMemento restore(int index) {
        FeedbackDraftMemento memento = snapshots.get(index);
        trace("Feedback draft history returned a saved memento snapshot");
        return memento;
    }

    public List<FeedbackDraftMemento> findAll() {
        return List.copyOf(snapshots);
    }

    private void trace(String description) {
        if (traceService != null) {
            traceService.recordPhase5(
                    OfficialPattern.MEMENTO,
                    getClass().getSimpleName(),
                    "Manage feedback draft history",
                    description,
                    "Instructor review"
            );
        }
    }
}
