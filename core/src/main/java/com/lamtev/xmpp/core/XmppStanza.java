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
    @Nullable
    private final String lang;

    public XmppStanza(@NotNull final Kind kind, @NotNull final String id, @NotNull final TypeAttribute type, @NotNull final Entry entry) {
        this.kind = kind;
        this.id = id;
        this.type = type;
        this.entry = entry;
        this.from = null;
        this.to = null;
        this.lang = null;
    }

    public XmppStanza(@NotNull final Kind kind, @NotNull final String id, @NotNull final TypeAttribute type, @NotNull final Entry entry, @NotNull final String from, @NotNull final String to, @NotNull final String lang) {
        this.kind = kind;
        this.id = id;
        this.type = type;
        this.entry = entry;
        this.from = from;
        this.to = to;
        this.lang = lang;
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

    @Nullable
    public String lang() {
        return lang;
    }

    @Override
    public int code() {
        return CODE_STANZA;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        XmppStanza that = (XmppStanza) o;

        if (kind != that.kind) return false;
        if (!id.equals(that.id)) return false;
        if (!type.equals(that.type)) return false;
        if (!entry.equals(that.entry)) return false;
        if (from != null ? !from.equals(that.from) : that.from != null) return false;
        if (to != null ? !to.equals(that.to) : that.to != null) return false;

        return lang != null ? lang.equals(that.lang) : that.lang == null;
    }

    @Override
    public int hashCode() {
        int result = kind.hashCode();
        result = 31 * result + id.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + entry.hashCode();
        result = 31 * result + (from != null ? from.hashCode() : 0);
        result = 31 * result + (to != null ? to.hashCode() : 0);
        result = 31 * result + (lang != null ? lang.hashCode() : 0);

        return result;
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
        NORMAL("normal"),
        CHAT("chat"),
        GROUPCHAT("groupchat"),
        HEADLINE("headline"),
        ERROR("error")
        ;

        @NotNull
        private final String string;

        MessageTypeAttribute(@NotNull final String string) {
            this.string = string;
        }

        static MessageTypeAttribute of(@NotNull final String string) {
            if (NORMAL.string.equals(string)) {
                return NORMAL;
            } else if (CHAT.string.equals(string)) {
                return CHAT;
            } else if (GROUPCHAT.string.equals(string)) {
                return GROUPCHAT;
            } else if (HEADLINE.string.equals(string)) {
                return HEADLINE;
            } else if (ERROR.string.equals(string)) {
                return ERROR;
            }

            throw new IllegalArgumentException();
        }

        @Override
        public String toString() {
            return string;
        }
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
                case MESSAGE:
                    return MessageTypeAttribute.of(string);
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            IqBind iqBind = (IqBind) o;

            if (resource != null ? !resource.equals(iqBind.resource) : iqBind.resource != null) return false;
            return jid != null ? jid.equals(iqBind.jid) : iqBind.jid == null;
        }

        @Override
        public int hashCode() {
            int result = resource != null ? resource.hashCode() : 0;
            result = 31 * result + (jid != null ? jid.hashCode() : 0);
            return result;
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MessageBody that = (MessageBody) o;

            return body.equals(that.body);
        }

        @Override
        public int hashCode() {
            return body.hashCode();
        }
    }
}
