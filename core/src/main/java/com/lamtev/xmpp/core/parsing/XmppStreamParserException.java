package com.lamtev.xmpp.core.parsing;

import org.jetbrains.annotations.NotNull;

public final class XmppStreamParserException extends Exception {
    XmppStreamParserException(@NotNull String message, @NotNull Throwable t) {
        super(message, t);
    }
}
