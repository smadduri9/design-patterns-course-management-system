package edu.university.cms.application;

public record DashboardCountsResponse(
        int courses,
        int students,
        int assignments,
        int submissions,
        int traceEvents
) {
}
