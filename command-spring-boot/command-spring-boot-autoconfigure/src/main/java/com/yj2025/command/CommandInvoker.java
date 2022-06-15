package com.yj2025.command;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class CommandInvoker {

    private final List<Command> commandList = new ArrayList<>();
    private final List<Supplier<Boolean>> predicates = new ArrayList<>();

    public CommandInvoker add(Command command) {
        commandList.add(command);
        predicates.add(() -> true);
        return this;
    }

    public CommandInvoker add(Command command, Supplier<Boolean> booleanSupplier) {
        commandList.add(command);
        predicates.add(booleanSupplier);
        return this;
    }


    public CommandInvoker clear() {
        commandList.clear();
        predicates.clear();
        return this;
    }

    public CommandInvoker execute() {
        for (int i = 0; i < commandList.size(); i++) {
            if (predicates.get(i).get()) {
                commandList.get(i).execute();
            }
        }
        return this;
    }

    public List<Command> getCommands() {
        return commandList;
    }
}
