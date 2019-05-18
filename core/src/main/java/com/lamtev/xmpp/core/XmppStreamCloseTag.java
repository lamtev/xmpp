package com.lamtev.xmpp.core;

import org.jetbrains.annotations.NotNull;

public final class XmppStreamCloseTag implements XmppUnit {
    private XmppStreamCloseTag() {}

    @NotNull
    public static XmppStreamCloseTag instance() {
        return Holder.OUTER_INSTANCE;
    }

    @Override
    public int code() {
        return CODE_STREAM_CLOSE;
    }

    private static final class Holder {
        private static final XmppStreamCloseTag OUTER_INSTANCE = new XmppStreamCloseTag();
    }
}
