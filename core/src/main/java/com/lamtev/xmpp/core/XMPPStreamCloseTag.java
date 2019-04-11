package com.lamtev.xmpp.core;

import org.jetbrains.annotations.NotNull;

public final class XMPPStreamCloseTag implements XMPPUnit {
    @NotNull
    public static final XMPPStreamCloseTag INSTANCE = new XMPPStreamCloseTag();

    private XMPPStreamCloseTag() {}
}
