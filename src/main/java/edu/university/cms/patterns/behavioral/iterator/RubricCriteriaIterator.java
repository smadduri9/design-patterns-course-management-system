package edu.university.cms.patterns.behavioral.iterator;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.OfficialPattern;
import edu.university.cms.domain.Rubric;
import edu.university.cms.domain.RubricCriterion;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class RubricCriteriaIterator implements Iterator<RubricCriterion> {

    private final List<RubricCriterion> criteria;
    private int index;

    public RubricCriteriaIterator(Rubric rubric) {
        this(rubric, null);
    }

    public RubricCriteriaIterator(Rubric rubric, PatternTraceService traceService) {
        this.criteria = Objects.requireNonNull(rubric, "rubric is required").getCriteria();
        if (traceService != null) {
            traceService.recordPhase2(
                    OfficialPattern.ITERATOR,
                    getClass().getSimpleName(),
                    "Traverse rubric criteria",
                    "Iterator prepared rubric criteria traversal without exposing storage details",
                    "Render rubric"
            );
        }
    }

    @Override
    public boolean hasNext() {
        return index < criteria.size();
    }

    @Override
    public RubricCriterion next() {
        return criteria.get(index++);
    }
}
