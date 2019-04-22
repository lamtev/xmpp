package com.lamtev.xmpp.core;

public final class XMPPStanza implements XMPPUnit {

    @Override
    public int code() {
        return CODE_STANZA;
    }
}
