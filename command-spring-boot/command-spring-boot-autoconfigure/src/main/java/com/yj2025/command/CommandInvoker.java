package com.yj2025.command;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author liuyuhua
 */
public final class CommandInvoker {

    private Table<Command<? extends Object>, Consumer<? extends Object>, Supplier<Boolean>> commandTable = HashBasedTable.create();

    public CommandInvoker() {
    }

    private final List<Command> commandList = new ArrayList<>();
    private final List<Supplier<Boolean>> predicates = new ArrayList<>();

    public <T> void add(Command<T> command) {
        Assert.notNull(command, "command不能为空");
        commandTable.put(command, o -> {
        }, () -> true);
    }

    public <T> void add(Command<T> command, Supplier<Boolean> predicate) {
        Assert.notNull(command, "command不能为空");
        Assert.notNull(predicate, "predicate不能为空");
        commandTable.put(command, o -> {
        }, predicate);
    }

    public <T> void add(Command<T> command, Consumer<T> consumer) {
        Assert.notNull(command, "command不能为空");
        Assert.notNull(consumer, "consumer不能为空");
        commandTable.put(command, consumer, () -> true);
    }

    public <T> void add(Command<T> command, Consumer<T> consumer, Supplier<Boolean> predicate) {
        Assert.notNull(command, "command不能为空");
        Assert.notNull(predicate, "predicate不能为空");
        Assert.notNull(consumer, "consumer不能为空");
        commandTable.put(command, consumer, predicate);
    }

    public void clear() {
        commandList.clear();
        predicates.clear();
    }

    public void execute() {
        List<Table.Cell<Command<?>, Consumer<?>, Supplier<Boolean>>> collect = commandTable.cellSet().stream().filter(cell -> cell.getValue().get()).collect(Collectors.toList());
        for (Table.Cell<Command<?>, Consumer<?>, Supplier<Boolean>> cell : collect) {
            if (cell.getValue() != null) {
                Object result = cell.getRowKey().execute();
                Consumer consumer = cell.getColumnKey();
                consumer.accept(result);
            }
        }
    }
}
