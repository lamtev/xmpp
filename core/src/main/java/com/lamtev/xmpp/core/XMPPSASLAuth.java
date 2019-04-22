package com.lamtev.xmpp.core;

import org.jetbrains.annotations.NotNull;

public class XMPPSASLAuth implements XMPPUnit {
    @NotNull
    private final XMPPStreamFeatures.Type.SASLMechanism mechanism;
    @NotNull
    private final String body;

    public XMPPSASLAuth(@NotNull final XMPPStreamFeatures.Type.SASLMechanism mechanism, @NotNull final String body) {
        this.mechanism = mechanism;
        this.body = body;
    }

    public XMPPStreamFeatures.Type.SASLMechanism mechanism() {
        return mechanism;
    }

    public String body() {
        return body;
    }

    @Override
    public int code() {
        //TODO:
        return 0;
    }
}
