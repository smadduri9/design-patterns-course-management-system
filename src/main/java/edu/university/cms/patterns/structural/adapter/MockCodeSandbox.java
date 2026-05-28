package edu.university.cms.patterns.structural.adapter;

import java.util.List;

public class MockCodeSandbox {

    public MockSandboxResponse execute(MockSandboxRequest request) {
        boolean hasClass = request.source().contains("class ");
        boolean hasReturn = request.source().contains("return");
        return new MockSandboxResponse(List.of(
                new MockSandboxCase("compiles", hasClass, hasClass ? "Class declaration found" : "Missing class declaration"),
                new MockSandboxCase("returnsValue", hasReturn, hasReturn ? "Return statement found" : "Missing return statement")
        ));
    }

    public record MockSandboxRequest(String source, String language) {
    }

    public record MockSandboxResponse(List<MockSandboxCase> cases) {
    }

    public record MockSandboxCase(String name, boolean ok, String details) {
    }
}
