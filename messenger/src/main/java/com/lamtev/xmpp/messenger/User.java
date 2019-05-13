package com.lamtev.xmpp.messenger;

import com.lamtev.xmpp.core.io.XmppExchange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import static com.lamtev.xmpp.core.io.XmppExchange.State.*;

public class User {
    @NotNull
    private final String id;
    @Nullable
    private String jid;
    //TODO: construct in accordance with config
    @NotNull
    public final Deque<XmppExchange.State> stateQueue = new ArrayDeque<>(List.of(SASL_NEGOTIATION, RESOURCE_BINDING, EXCHANGE));

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
