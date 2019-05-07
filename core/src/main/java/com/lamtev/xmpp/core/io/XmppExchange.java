package com.lamtev.xmpp.core.io;

import org.jetbrains.annotations.NotNull;

import static com.lamtev.xmpp.core.io.XmppExchange.State.*;

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
        this.state = WAITING_FOR_STREAM_HEADER;

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

    void changeState(@NotNull final State state) {
        System.out.println("State set: " + state);
        this.state = state;

        if (state == WAITING_FOR_STREAM_HEADER) {
            initialStream.reopen();
        }
    }

    public enum State {
        WAITING_FOR_STREAM_HEADER,
        TLS_NEGOTIATION,
        SASL_NEGOTIATION,
        RESOURCE_BINDING,
        EXCHANGE
    }
}
