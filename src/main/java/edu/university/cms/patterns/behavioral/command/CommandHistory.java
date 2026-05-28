package edu.university.cms.patterns.behavioral.command;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class CommandHistory {

    private final List<CommandHistoryEntry> entries = new ArrayList<>();

    public void add(String commandName) {
        entries.add(new CommandHistoryEntry(commandName, Instant.now()));
    }

    public List<CommandHistoryEntry> findAll() {
        return List.copyOf(entries);
    }

    public record CommandHistoryEntry(String commandName, Instant executedAt) {
    }
}
