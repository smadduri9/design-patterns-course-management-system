package edu.university.cms.patterns.structural.adapter;

import edu.university.cms.domain.TestResult;

import java.util.List;

public interface SandboxRunner {

    List<TestResult> runTests(String javaSource);
}
