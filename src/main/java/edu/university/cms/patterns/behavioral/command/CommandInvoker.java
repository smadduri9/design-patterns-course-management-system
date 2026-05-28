package edu.university.cms.patterns.behavioral.command;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.OfficialPattern;

import java.util.Objects;

public class CommandInvoker {

    private final CommandHistory history;
    private final PatternTraceService traceService;

    public CommandInvoker(CommandHistory history, PatternTraceService traceService) {
        this.history = Objects.requireNonNull(history, "history is required");
        this.traceService = traceService;
    }

    public <T> T execute(CourseCommand<T> command) {
        CourseCommand<T> safeCommand = Objects.requireNonNull(command, "command is required");
        T result = safeCommand.execute();
        history.add(safeCommand.name());
        if (traceService != null) {
            traceService.recordPhase2(
                    OfficialPattern.COMMAND,
                    safeCommand.getClass().getSimpleName(),
                    safeCommand.name(),
                    "Instructor action executed through a command object",
                    "Instructor creates course or assignment"
            );
        }
        return result;
    }

    public CommandHistory getHistory() {
        return history;
    }
}
