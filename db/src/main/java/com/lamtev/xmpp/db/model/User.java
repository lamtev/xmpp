package com.lamtev.xmpp.db.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class User {
    @NotNull
    private final String jidLocalPart;
    @Nullable
    private final String firstName;
    @Nullable
    private final String lastName;

    public User(@NotNull final String jidLocalPart, @Nullable final String firstName, @Nullable final String lastName) {
        this.jidLocalPart = jidLocalPart;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @NotNull
    public String jidLocalPart() {
        return jidLocalPart;
    }

    @Nullable
    public String firstName() {
        return firstName;
    }

    @Nullable
    public String lastName() {
        return lastName;
    }
}
