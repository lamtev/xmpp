package com.lamtev.xmpp.messenger;

import com.lamtev.xmpp.core.io.XmppExchange;
import com.lamtev.xmpp.db.model.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import static com.lamtev.xmpp.core.io.XmppExchange.State.*;

//TODO
public class UserHandler {
    //TODO: construct in accordance with config
    @NotNull
    public final Deque<XmppExchange.State> stateQueue = new ArrayDeque<>(List.of(SASL_NEGOTIATION, RESOURCE_BINDING, EXCHANGE));
    @Nullable
    private User user;
    @Nullable
    private String resource;

    public void setUser(@NotNull final User user) {
        this.user = user;
    }

    @Nullable
    public User user() {
        return user;
    }

    @Nullable
    public String resource() {
        return resource;
    }

    public void setResource(@Nullable final String resource) {
        this.resource = resource;
    }
}
