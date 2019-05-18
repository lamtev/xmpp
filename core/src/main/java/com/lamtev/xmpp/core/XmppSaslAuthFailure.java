package com.lamtev.xmpp.core;

import org.jetbrains.annotations.NotNull;

public class XmppSaslAuthFailure implements XmppUnit {
    @NotNull
    public static final String NAMESPACE = "urn:ietf:params:xml:ns:xmpp-sasl";

    private XmppSaslAuthFailure() {}

    @NotNull
    public static XmppSaslAuthFailure instance() {
        return Holder.OUTER_INSTANCE;
    }

    @Override
    public int code() {
        return CODE_SASL_AUTH_FAILURE;
    }

    private static final class Holder {
        private static final XmppSaslAuthFailure OUTER_INSTANCE = new XmppSaslAuthFailure();
    }
}
