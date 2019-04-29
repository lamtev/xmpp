package com.lamtev.xmpp.core.io;

import org.jetbrains.annotations.NotNull;

public final class XmppExchange {
    @NotNull
    private final XmppInputStream initialStream;
    @NotNull
    private final XmppOutputStream responseStream;
    @NotNull
    private State state;

    public XmppExchange(@NotNull final XmppInputStream initialStream, @NotNull final XmppOutputStream responseStream) {
        this.initialStream = initialStream;
        this.responseStream = responseStream;
        this.state = State.INITIAL;

        this.initialStream.setExchange(this);
        this.responseStream.setExchange(this);
    }

    @NotNull
    public XmppInputStream initialStream() {
        return initialStream;
    }

    @NotNull
    public XmppOutputStream responseStream() {
        return responseStream;
    }

    @NotNull
    public State state() {
        return state;
    }

    void setState(@NotNull final State state) {
        this.state = state;

        if (state == State.RESOURCE_BINDING) {
            initialStream.reopen();
        }
    }

    public enum State {
        INITIAL,
        TLS_NEGOTIATION,
        SASL_NEGOTIATION,
        RESOURCE_BINDING,
        EXCHANGE
    }
}
