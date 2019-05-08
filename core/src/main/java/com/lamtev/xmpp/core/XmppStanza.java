package com.lamtev.xmpp.core;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class XmppStanza implements XmppUnit {
    @NotNull
    private final Kind kind;
    @NotNull
    private final String id;
    @NotNull
    private final TypeAttribute type;
    @NotNull
    private final XmppStanza.TopElement topElement;
    @Nullable
    private final String from;
    @Nullable
    private final String to;
    @Nullable
    private final String lang;

    public XmppStanza(@NotNull final Kind kind, @NotNull final String id, @NotNull final TypeAttribute type, @NotNull final XmppStanza.TopElement topElement) {
        this.kind = kind;
        this.id = id;
        this.type = type;
        this.topElement = topElement;
        this.from = null;
        this.to = null;
        this.lang = null;
    }

    public XmppStanza(@NotNull final Kind kind, @NotNull final String id, @NotNull final TypeAttribute type, @Nullable final String from, @Nullable final String to, @Nullable final String lang, @NotNull final XmppStanza.TopElement topElement) {
        this.kind = kind;
        this.id = id;
        this.type = type;
        this.topElement = topElement;
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
    public XmppStanza.TopElement topElement() {
        return topElement;
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
        if (!topElement.equals(that.topElement)) return false;
        if (from != null ? !from.equals(that.from) : that.from != null) return false;
        if (to != null ? !to.equals(that.to) : that.to != null) return false;

        return lang != null ? lang.equals(that.lang) : that.lang == null;
    }

    @Override
    public int hashCode() {
        int result = kind.hashCode();
        result = 31 * result + id.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + topElement.hashCode();
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
        GET("get"),
        ERROR("error"),
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
            } else if (GET.string.equals(string)) {
                return GET;
            }

            throw new IllegalArgumentException();
        }

        @Override
        public String toString() {
            return string;
        }
    }

    public interface TopElement {
        int CODE_IQ_BIND_CODE = 0;
        int CODE_IQ_QUERY_CODE = 1;
        int CODE_IQ_ERROR = 2;

        int CODE_MESSAGE_BODY_CODE = 3;

        int CODE_UNSUPPORTED = 4;

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

    public static final class IqBind implements TopElement {
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
            return CODE_IQ_BIND_CODE;
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

    public static final class MessageBody implements TopElement {
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
            return CODE_MESSAGE_BODY_CODE;
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

    public static final class IqQuery implements TopElement {
        @NotNull
        private final ContentNamespace namespace;
        @Nullable
        private String version;
        @Nullable
        private List<Item> items;

        public IqQuery(@NotNull final ContentNamespace namespace, @Nullable final String version, @Nullable final List<Item> items) {
            this.namespace = namespace;
            this.version = version;
            this.items = items;
        }

        @NotNull
        public ContentNamespace namespace() {
            return namespace;
        }

        @Nullable
        public String version() {
            return version;
        }

        @Nullable
        public List<Item> items() {
            return items;
        }

        @Override
        public int code() {
            return CODE_IQ_QUERY_CODE;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final IqQuery iqQuery = (IqQuery) o;

            if (namespace != iqQuery.namespace) return false;
            if (version != null ? !version.equals(iqQuery.version) : iqQuery.version != null) { return false;}

            return items != null ? items.equals(iqQuery.items) : iqQuery.items == null;
        }

        @Override
        public int hashCode() {
            int result = namespace.hashCode();
            result = 31 * result + (version != null ? version.hashCode() : 0);
            result = 31 * result + (items != null ? items.hashCode() : 0);

            return result;
        }

        public enum ContentNamespace {
            ROSTER("jabber:iq:roster"),
            ;

            @NotNull
            private final String string;

            ContentNamespace(@NotNull final String string) {
                this.string = string;
            }

            @Override
            public String toString() {
                return string;
            }
        }

        public static final class Item {
            @NotNull
            private final String jid;

            public Item(@NotNull final String jid) {
                this.jid = jid;
            }

            public String jid() {
                return jid;
            }

            @Override
            public boolean equals(final Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                final Item item = (Item) o;

                return jid.equals(item.jid);

            }

            @Override
            public int hashCode() {
                return jid.hashCode();
            }
        }
    }

    public static final class UnsupportedElement implements TopElement {
        @NotNull
        public final String name;

        public UnsupportedElement(@NotNull final String name) {
            this.name = name;
        }

        @Override
        public int code() {
            return CODE_UNSUPPORTED;
        }
    }

    public static final class IqError implements TopElement {
        @NotNull
        public final String type;

        public IqError(@NotNull final String type) {
            this.type = type;
        }

        @Override
        public int code() {
            return CODE_IQ_ERROR;
        }
    }
}
