package com.lamtev.xmpp.core.io;

import org.jetbrains.annotations.NotNull;

public final class XMPPExchange {
    @NotNull
    private final XMPPInputStream initialStream;
    @NotNull
    private final XMPPOutputStream responseStream;
    @NotNull
    private State state;

    public XMPPExchange(@NotNull final XMPPInputStream initialStream, @NotNull final XMPPOutputStream responseStream) {
        this.initialStream = initialStream;
        this.responseStream = responseStream;
        this.state = State.INITIAL;

        this.initialStream.setExchange(this);
        this.responseStream.setExchange(this);
    }

    @NotNull
    public XMPPInputStream initialStream() {
        return initialStream;
    }

    @NotNull
    public XMPPOutputStream responseStream() {
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
