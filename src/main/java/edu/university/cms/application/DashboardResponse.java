package edu.university.cms.application;

public record DashboardResponse(
        UserResponse instructor,
        DashboardCountsResponse counts
) {
}
