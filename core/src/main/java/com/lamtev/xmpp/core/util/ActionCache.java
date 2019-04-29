package com.lamtev.xmpp.core.util;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ActionCache<E extends Enum<E>> {

    @NotNull
    private final Consumer<E>[] actions;

    public ActionCache(@NotNull final Class<E> clazz, @NotNull final Consumer<E>... actions) {
        if (clazz.getEnumConstants().length != actions.length) {
            throw new IllegalArgumentException();
        }

        this.actions = actions;
    }

    @NotNull
    public Consumer<E> get(@NotNull final E val) {
        return actions[val.ordinal()];
    }

}
