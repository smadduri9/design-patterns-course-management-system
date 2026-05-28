package edu.university.cms.web;

import edu.university.cms.application.PatternResponse;
import edu.university.cms.domain.OfficialPattern;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/app")
public class AppPatternController {

    @GetMapping("/patterns")
    public List<PatternResponse> patterns() {
        return Arrays.stream(OfficialPattern.values())
                .map(PatternResponse::from)
                .toList();
    }
}
