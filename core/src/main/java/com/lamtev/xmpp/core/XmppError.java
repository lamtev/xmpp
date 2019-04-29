package com.lamtev.xmpp.core;

public enum XmppError implements XmppUnit {
    INVALID_NAMESPACE,
    ;

    @Override
    public int code() {
        return CODE_ERROR;
    }
}
