package com.lamtev.xmpp.core;

import org.jetbrains.annotations.NotNull;

public final class XmppStanza implements XmppUnit {
    @NotNull
    private final Kind kind;
    @NotNull
    private final String id;
    @NotNull
    private final String resource;

    public XmppStanza(@NotNull final Kind kind, @NotNull final String id, @NotNull final String resource) {
        this.kind = kind;
        this.id = id;
        this.resource = resource;
    }

    @NotNull
    public Kind kind() {
        return kind;
    }

    @NotNull
    public String id() {
        return id;
    }

    @NotNull
    public String resource() {
        return resource;
    }

    @Override
    public int code() {
        return CODE_STANZA;
    }

    public enum Kind {
        MESSAGE("message"),
        PRESENCE("presence"),
        IQ("iq");

        @NotNull
        private final String string;

        Kind(@NotNull final String string) {
            this.string = string;
        }

        public static Kind of(@NotNull final String string) {
            if (MESSAGE.string.equals(string)) {
                return MESSAGE;
            } else if (PRESENCE.string.equals(string)) {
                return PRESENCE;
            } else if (IQ.string.equals(string)) {
                return IQ;
            }

            throw new IllegalArgumentException("No enum constant for string value \"" + string + "\"");
        }

        @Override
        public String toString() {
            return string;
        }
    }

    public enum MessageTypeAttribute implements TypeAttribute {
        NORMAL,
        CHAT,
        GROUPCHAT,
        HEADLINE,
        ERROR
    }

    public enum IqTypeAttribute implements TypeAttribute {
    }

    public interface TypeAttribute {
        static TypeAttribute of(@NotNull final Kind kind, @NotNull final String string) {
            return MessageTypeAttribute.NORMAL;
        }
    }

}
