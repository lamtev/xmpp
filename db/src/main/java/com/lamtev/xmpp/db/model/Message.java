package com.lamtev.xmpp.db.model;

import org.jetbrains.annotations.NotNull;

public final class Message {
    @NotNull
    public final String senderJidLocalPart;
    @NotNull
    public final String recipientJidLocalPart;
    @NotNull
    public final String text;
    public final double timestamp;
    public boolean isDelivered;

    public Message(final @NotNull String senderJidLocalPart, final @NotNull String recipientJidLocalPart, final @NotNull String text, final double timestamp) {
        this.senderJidLocalPart = senderJidLocalPart;
        this.recipientJidLocalPart = recipientJidLocalPart;
        this.text = text;
        this.timestamp = timestamp;
    }
}
