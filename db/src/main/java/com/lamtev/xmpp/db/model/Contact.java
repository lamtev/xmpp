package com.lamtev.xmpp.db.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Contact {
    @NotNull
    public final String jidLocalPart;
    @Nullable
    public final String name;
    @Nullable
    public final String subscription;

    public Contact(final @NotNull String jidLocalPart, final @Nullable String name, final @Nullable String subscription) {
        this.jidLocalPart = jidLocalPart;
        this.name = name;
        this.subscription = subscription;
    }
}
