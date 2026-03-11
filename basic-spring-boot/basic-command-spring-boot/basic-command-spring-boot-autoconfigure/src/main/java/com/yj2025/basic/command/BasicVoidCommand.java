package com.yj2025.basic.command;

public abstract class BasicVoidCommand extends BasicCommand<Void> {

    protected abstract void perform() throws Exception;

    public BasicVoidCommand() {
        super();
    }

    @Override
    protected Void doExecute() throws Exception {
        perform();
        return null;
    }
}
