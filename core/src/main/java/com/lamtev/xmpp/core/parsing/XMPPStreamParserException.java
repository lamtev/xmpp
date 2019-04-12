package com.lamtev.xmpp.core.parsing;

import org.jetbrains.annotations.NotNull;

public final class XMPPStreamParserException extends Exception {
    XMPPStreamParserException(@NotNull String message, @NotNull Throwable t) {
        super(message, t);
    }
}
