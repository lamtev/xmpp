package com.lamtev.xmpp.core;

import org.jetbrains.annotations.NotNull;

public class XmppSaslAuthSuccess implements XmppUnit {
    @NotNull
    public static final String NAMESPACE = "urn:ietf:params:xml:ns:xmpp-sasl";

    private XmppSaslAuthSuccess() {}

    @NotNull
    public static XmppSaslAuthSuccess instance() {
        return Holder.OUTER_INSTANCE;
    }

    @Override
    public int code() {
        return CODE_SASL_AUTH_SUCCESS;
    }

    private static final class Holder {
        private static final XmppSaslAuthSuccess OUTER_INSTANCE = new XmppSaslAuthSuccess();
    }
}
