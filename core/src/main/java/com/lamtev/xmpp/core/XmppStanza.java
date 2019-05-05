package com.lamtev.xmpp.core;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class XmppStanza implements XmppUnit {
    @NotNull
    private final Kind kind;
    @NotNull
    private final String id;
    @NotNull
    private final TypeAttribute type;
    @NotNull
    private final Entry entry;
    @Nullable
    private final String from;
    @Nullable
    private final String to;

    public XmppStanza(@NotNull final Kind kind, @NotNull final String id, @NotNull final TypeAttribute type, @NotNull final Entry entry) {
        this.kind = kind;
        this.id = id;
        this.type = type;
        this.entry = entry;
        this.from = null;
        this.to = null;
    }

    public XmppStanza(@NotNull final Kind kind, @NotNull final String id, @NotNull final TypeAttribute type, @NotNull final Entry entry, @NotNull final String from, @NotNull final String to) {
        this.kind = kind;
        this.id = id;
        this.type = type;
        this.entry = entry;
        this.from = from;
        this.to = to;
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
    public TypeAttribute type() {
        return type;
    }

    @NotNull
    public Entry entry() {
        return entry;
    }

    @Nullable
    public String from() {
        return from;
    }

    @Nullable
    public String to() {
        return to;
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

        @NotNull
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
        SET("set"),
        RESULT("result"),
        ;

        @NotNull
        private final String string;

        IqTypeAttribute(@NotNull final String string) {
            this.string = string;
        }

        static IqTypeAttribute of(@NotNull final String string) {
            if (SET.string.equals(string)) {
                return SET;
            } else if (RESULT.string.equals(string)) {
                return RESULT;
            }

            throw new IllegalArgumentException();
        }

        @Override
        public String toString() {
            return string;
        }
    }

    public interface Entry {
        int IQ_BIND_CODE = 0;
        int MESSAGE_BODY_CODE = 1;

        int code();
    }

    public interface TypeAttribute {
        @NotNull
        static TypeAttribute of(@NotNull final Kind kind, @NotNull final String string) {
            switch (kind) {
                case IQ:
                    return IqTypeAttribute.of(string);
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    public static final class IqBind implements Entry {
        @Nullable
        private final String resource;
        @Nullable
        private final String jid;

        @Contract("!null, !null -> fail")
        public IqBind(@Nullable final String resource, @Nullable final String jid) {
            this.resource = resource;
            this.jid = jid;
        }

        @Nullable
        public String resource() {
            return resource;
        }

        @Nullable
        public String jid() {
            return jid;
        }

        @Override
        public int code() {
            return IQ_BIND_CODE;
        }
    }

    public static final class MessageBody implements Entry {
        @NotNull
        private final String body;

        public MessageBody(@NotNull final String body) {
            this.body = body;
        }

        @NotNull
        public String body() {
            return body;
        }

        @Override
        public int code() {
            return MESSAGE_BODY_CODE;
        }
    }
}
