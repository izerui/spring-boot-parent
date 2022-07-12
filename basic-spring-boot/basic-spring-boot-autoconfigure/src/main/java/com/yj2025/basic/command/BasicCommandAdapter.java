package com.yj2025.basic.command;

public abstract class BasicCommandAdapter<R> extends BasicCommand<R> {

    protected abstract BasicCommand<R> withCommand();

    @Override
    protected final R doExecute() throws Exception {
        return withCommand().execute();
    }
}
