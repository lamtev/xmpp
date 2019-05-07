package com.lamtev.xmpp.messenger;

import com.lamtev.xmpp.core.io.XmppExchange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;

import static com.lamtev.xmpp.core.io.XmppExchange.State.*;
import static java.util.Arrays.asList;

public class User {
    @NotNull
    private final String id;
    @Nullable
    private String jid;
    //TODO: construct in accordance with config
    @NotNull
    public final Deque<XmppExchange.State> stateQueue = new ArrayDeque<>(asList(SASL_NEGOTIATION, RESOURCE_BINDING, EXCHANGE));

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
