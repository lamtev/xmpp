package com.lamtev.xmpp.core.io;

import org.jetbrains.annotations.NotNull;

public class XMPPExchange {
    @NotNull
    private final XMPPInputStream initialStream;
    @NotNull
    private final XMPPOutputStream responseStream;

    public XMPPExchange(@NotNull final XMPPInputStream initialStream, @NotNull final XMPPOutputStream responseStream) {

        this.initialStream = initialStream;
        this.responseStream = responseStream;
    }

    public XMPPInputStream initialStream() {
        return initialStream;
    }

    public XMPPOutputStream responseStream() {
        return responseStream;
    }
}
