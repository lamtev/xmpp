package com.lamtev.xmpp.core.io;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public final class XMPPIOException extends IOException {
    XMPPIOException(@NotNull final String message, @NotNull final Throwable t) {
        super(message, t.getCause());
    }
}
