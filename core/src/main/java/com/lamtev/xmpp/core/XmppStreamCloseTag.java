package com.lamtev.xmpp.core;

import org.jetbrains.annotations.NotNull;

public final class XmppStreamCloseTag implements XmppUnit {
    @NotNull
    public static final XmppStreamCloseTag INSTANCE = new XmppStreamCloseTag();

    private XmppStreamCloseTag() {}

    @Override
    public int code() {
        return 3;
    }
}
