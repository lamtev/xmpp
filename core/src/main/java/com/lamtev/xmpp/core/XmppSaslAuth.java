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
        return CODE_SASL_AUTH;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        XmppSaslAuth that = (XmppSaslAuth) o;

        if (mechanism != that.mechanism) return false;

        return body.equals(that.body);
    }

    @Override
    public int hashCode() {
        int result = mechanism.hashCode();
        result = 31 * result + body.hashCode();
        return result;
    }
}
