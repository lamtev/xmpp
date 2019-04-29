package com.lamtev.xmpp.core;

import org.jetbrains.annotations.NotNull;

public class XmppSaslAuthSuccess implements XmppUnit {
    @NotNull
    public static final String NAMESPACE = "urn:ietf:params:xml:ns:xmpp-sasl";

    @Override
    public int code() {
        return CODE_SASL_AUTH_SUCCESS;
    }
}
