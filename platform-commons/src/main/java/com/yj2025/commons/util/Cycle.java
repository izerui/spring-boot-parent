package com.yj2025.commons.util;

import java.util.Collection;

/**
 * Created by serv on 2017/7/1.
 */
public abstract class Cycle<T> {

    public void cycle(T t) {
        loop(t, null, 0);
    }

    public void cycle(Collection<T> list) {
        for (T item : list) {
            loop(item, null, 0);
        }
    }

    private void loop(T item, T parent, int level) {

        boolean loopChild = performWithLoopChild(item, parent, level);

        if (loopChild) {
            Collection<T> children = getChildren(item);

            if (children != null) {
                for (T child : children) {
                    loop(child, item, level + 1);
                }
            }
        }

        performed(item, parent, level);

    }

    protected void perform(T item, T parent) {

    }

    protected void perform(T item, T parent, int level) {
        this.perform(item, parent);
    }

    protected boolean performWithLoopChild(T item, T parent, int level) {
        this.perform(item, parent, level);
        return true;
    }

    protected void performed(T item, T parent) {

    }

    protected void performed(T item, T parent, int level) {
        this.performed(item, parent);
    }

    protected abstract Collection<T> getChildren(T item);

}
