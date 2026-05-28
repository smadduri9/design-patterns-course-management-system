package edu.university.cms.application;

import java.util.List;

public record ScenarioResultView(
        String title,
        String status,
        String summary,
        List<String> details
) {
}
