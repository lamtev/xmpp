package com.lamtev.xmpp.core;

import org.jetbrains.annotations.NotNull;

public class XmppSaslAuth implements XmppUnit {
    @NotNull
    private final XmppStreamFeatures.Type.SASLMechanism mechanism;
    @NotNull
    private final String body;

    public XmppSaslAuth(@NotNull final XmppStreamFeatures.Type.SASLMechanism mechanism, @NotNull final String body) {
        this.mechanism = mechanism;
        this.body = body;
    }

    public XmppStreamFeatures.Type.SASLMechanism mechanism() {
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
