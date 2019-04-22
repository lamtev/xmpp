package com.lamtev.xmpp.core;

public enum XMPPError implements XMPPUnit {
    INVALID_NAMESPACE,
    ;

    @Override
    public int code() {
        return CODE_ERROR;
    }
}
