package com.lamtev.xmpp.messenger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class User {
    @NotNull
    private final String id;
    @Nullable
    private String jid;

    public User(@NotNull final String id) {
        this.id = id;
    }

    @Nullable
    public String jid() {
        return jid;
    }

    public void setJid(@NotNull final String jid) {
        this.jid = jid;
    }
}
