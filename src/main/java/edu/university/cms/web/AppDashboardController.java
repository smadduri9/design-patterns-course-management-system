package edu.university.cms.web;

import edu.university.cms.application.AppDashboardService;
import edu.university.cms.application.DashboardResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app")
public class AppDashboardController {

    private final AppDashboardService dashboardService;

    public AppDashboardController(AppDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        return dashboardService.dashboard();
    }
}
