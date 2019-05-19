package com.lamtev.xmpp.core;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

//TODO: may be extract many classes to new files
public final class XmppStanza implements XmppUnit {
    @NotNull
    private final Kind kind;
    @Nullable
    private final String to;
    @Nullable
    private final String from;
    @Nullable
    private final String id;
    @Nullable
    private final TypeAttribute type;
    @Nullable
    private final String lang;
    @NotNull
    private final TopElement topElement;

    public XmppStanza(@NotNull final Kind kind, @Nullable final String id, @Nullable final TypeAttribute type, @NotNull final TopElement topElement) {
        this(kind, null, null, id, type, null, topElement);
    }

    public XmppStanza(@NotNull final Kind kind, @Nullable final String to, @Nullable final String from, @Nullable final String id, @Nullable final TypeAttribute type, @Nullable final String lang, @NotNull final TopElement topElement) {
        this.kind = kind;
        this.to = to;
        this.from = from;
        this.id = id;
        this.type = type;
        this.lang = lang;
        this.topElement = topElement;
    }

    @NotNull
    public Kind kind() {
        return kind;
    }

    @Nullable
    public String id() {
        return id;
    }

    @Nullable
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
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final XmppStanza that = (XmppStanza) o;

        if (kind != that.kind) return false;
        if (!Objects.equals(to, that.to)) return false;
        if (!Objects.equals(from, that.from)) return false;
        if (!Objects.equals(id, that.id)) return false;
        if (!Objects.equals(type, that.type)) return false;
        if (!Objects.equals(lang, that.lang)) return false;
        return topElement.equals(that.topElement);
    }

    @Override
    public int hashCode() {
        int result = kind.hashCode();
        result = 31 * result + (to != null ? to.hashCode() : 0);
        result = 31 * result + (from != null ? from.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (lang != null ? lang.hashCode() : 0);
        result = 31 * result + topElement.hashCode();
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

        @NotNull
        public static IqTypeAttribute of(@NotNull final String string) {
            if (SET.string.equals(string)) {
                return SET;
            } else if (RESULT.string.equals(string)) {
                return RESULT;
            } else if (GET.string.equals(string)) {
                return GET;
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

    public enum PresenceTypeAttribute implements TypeAttribute {
        ERROR("error"),
        SUBSCRIBE("subscribe"),
        ;

        @NotNull
        private final String string;

        PresenceTypeAttribute(@NotNull final String string) {
            this.string = string;
        }

        @NotNull
        public static PresenceTypeAttribute of(@NotNull final String string) {
            if (ERROR.string.equals(string)) {
                return ERROR;
            } else if (SUBSCRIBE.string.equals(string)) {
                return SUBSCRIBE;
            }

            throw new IllegalArgumentException();
        }

        @Override
        public String toString() {
            return string;
        }
    }

    public interface TopElement {
        int CODE_IQ_BIND = 0;
        int CODE_IQ_QUERY = 1;
        int CODE_IQ_ERROR = 2;
        int CODE_IQ_UNSUPPORTED = 3;

        int CODE_MESSAGE_BODY = 4;
        int CODE_MESSAGE_ERROR = 5;

        int CODE_PRESENCE_EMPTY = 6;
        int CODE_PRESENCE_ERROR = 7;

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
                case PRESENCE:
                    return PresenceTypeAttribute.of(string);
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
            return CODE_IQ_BIND;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            IqBind iqBind = (IqBind) o;

            if (!Objects.equals(resource, iqBind.resource)) return false;
            return Objects.equals(jid, iqBind.jid);
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
            return CODE_MESSAGE_BODY;
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
        private List<? extends TopElement> topElements;

        public IqQuery(@NotNull final ContentNamespace namespace, @Nullable final String version, @Nullable final List<? extends TopElement> topElements) {
            this.namespace = namespace;
            this.version = version;
            this.topElements = topElements;
        }

        public IqQuery(@NotNull final ContentNamespace namespace) {
            this(namespace, null, null);
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
        public List<? extends TopElement> topElements() {
            return topElements;
        }

        @Override
        public int code() {
            return CODE_IQ_QUERY;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final IqQuery iqQuery = (IqQuery) o;

            if (!namespace.equals(iqQuery.namespace)) return false;
            if (!Objects.equals(version, iqQuery.version)) { return false;}

            return Objects.equals(topElements, iqQuery.topElements);
        }

        @Override
        public int hashCode() {
            int result = namespace.hashCode();
            result = 31 * result + (version != null ? version.hashCode() : 0);
            result = 31 * result + (topElements != null ? topElements.hashCode() : 0);

            return result;
        }

        public interface ContentNamespace {}

        public enum SupportedContentNamespace implements ContentNamespace {
            ROSTER("jabber:iq:roster"),
            ;

            @NotNull
            private final String string;

            SupportedContentNamespace(@NotNull final String string) {
                this.string = string;
            }

            @Override
            public String toString() {
                return string;
            }
        }

        public static final class UnsupportedContentNamespace implements ContentNamespace {
            @NotNull
            private final String string;

            public UnsupportedContentNamespace(final @NotNull String string) {
                this.string = string;
            }

            @Override
            public boolean equals(final Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                final var that = (UnsupportedContentNamespace) o;

                return string.equals(that.string);
            }

            @Override
            public int hashCode() {
                return string.hashCode();
            }

            @Override
            public String toString() {
                return string;
            }
        }

        //TODO: inner namespace interface
        public interface TopElement {
            int CODE_ITEM = 0;
            int CODE_UNSUPPORTED_ELEMENT = 1;

            int code();

            @Nullable
            Namespace namespace();

            interface Namespace {}
        }

        public static final class Item implements TopElement {
            @Nullable
            private Ask ask;
            @NotNull
            private final String jid;
            @Nullable
            private String name;
            @Nullable
            private Subscription subscription;

            @Nullable
            private Set<String> groups;

            public Item(@NotNull final String jid) {
                this(jid, null, (Subscription) null);
            }

            public Item(@NotNull final String jid, @Nullable final String name, @Nullable final Subscription subscription) {
                this(null, jid, name, subscription, null);
            }

            public Item(@NotNull final String jid, @Nullable final String name, @Nullable Set<String> groups) {
                this(null, jid, name, null, groups);
            }

            public Item(@Nullable final Ask ask, @NotNull final String jid, @Nullable final String name, @Nullable final Subscription subscription, @Nullable Set<String> groups) {
                this.ask = ask;
                this.jid = jid;
                this.name = name;
                this.subscription = subscription;
                this.groups = groups;
            }

            @Nullable
            public Ask ask() {
                return ask;
            }

            @NotNull
            public String jid() {
                return jid;
            }

            @Nullable
            public String name() {
                return name;
            }

            @Nullable
            public Subscription subscription() {
                return subscription;
            }

            @Nullable
            public Collection<String> groups() {
                return groups;
            }

            public void setGroups(@NotNull final Set<String> groups) {
                this.groups = groups;
            }

            @Override
            public boolean equals(final Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                final Item item = (Item) o;

                if (ask != item.ask) return false;
                if (!jid.equals(item.jid)) return false;
                if (!Objects.equals(name, item.name)) return false;
                if (subscription != item.subscription) return false;
                return Objects.equals(groups, item.groups);
            }

            @Override
            public int hashCode() {
                int result = ask != null ? ask.hashCode() : 0;
                result = 31 * result + jid.hashCode();
                result = 31 * result + (name != null ? name.hashCode() : 0);
                result = 31 * result + (subscription != null ? subscription.hashCode() : 0);
                result = 31 * result + (groups != null ? groups.hashCode() : 0);
                return result;
            }

            @Override
            public int code() {
                return CODE_ITEM;
            }

            @Override
            public @Nullable Namespace namespace() {
                return null;
            }

            public enum Subscription {
                NONE("none"),
                FROM("from"),
                TO("to"),
                BOTH("both"),
                REMOVE("remove")
                ;

                @NotNull
                private final String string;

                Subscription(@NotNull final String string) {
                    this.string = string;
                }

                @Nullable
                public static Subscription of(@Nullable final String string) {
                    if (NONE.string.equals(string)) {
                        return NONE;
                    } else if (FROM.string.equals(string)) {
                        return FROM;
                    } else if (TO.string.equals(string)) {
                        return TO;
                    } else if (BOTH.string.equals(string)) {
                        return BOTH;
                    } else if (REMOVE.string.equals(string)) {
                        return REMOVE;
                    }

                    return null;
                }

                @Override
                public String toString() {
                    return string;
                }
            }

            public enum Ask {
                SUBSCRIBE("subscribe"),
                ;

                @NotNull
                private final String string;

                Ask(@NotNull final String string) {
                    this.string = string;
                }

                @Nullable
                public static Ask of(@Nullable final String string) {
                    if (SUBSCRIBE.string.equals(string)) {
                        return SUBSCRIBE;
                    }

                    return null;
                }

                @Override
                public String toString() {
                    return string;
                }
            }
        }

        public static final class UnsupportedElement implements TopElement {
            @NotNull
            public final String name;
            @Nullable
            private final UnsupportedNamespace namespace;

            public UnsupportedElement(@NotNull final String name, @Nullable final String namespace) {
                this.name = name;
                if (namespace != null) {
                    this.namespace = new UnsupportedNamespace(namespace);
                } else {
                    this.namespace = null;
                }
            }

            @Override
            public int code() {
                return CODE_UNSUPPORTED_ELEMENT;
            }

            @Override
            @Nullable
            public Namespace namespace() {
                return namespace;
            }

            @Override
            public boolean equals(final Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                final UnsupportedElement that = (UnsupportedElement) o;

                if (!name.equals(that.name)) return false;
                return Objects.equals(namespace, that.namespace);
            }

            @Override
            public int hashCode() {
                int result = name.hashCode();
                result = 31 * result + (namespace != null ? namespace.hashCode() : 0);
                return result;
            }

            public static final class UnsupportedNamespace implements Namespace {
                @NotNull
                private final String string;

                public UnsupportedNamespace(@NotNull final String string) {
                    this.string = string;
                }

                @Override
                public boolean equals(final Object o) {
                    if (this == o) return true;
                    if (o == null || getClass() != o.getClass()) return false;

                    final UnsupportedNamespace that = (UnsupportedNamespace) o;

                    return string.equals(that.string);
                }

                @Override
                public int hashCode() {
                    return string.hashCode();
                }

                @Override
                public String toString() {
                    return string;
                }
            }
        }
    }

    public static final class UnsupportedElement implements TopElement {
        @NotNull
        public final String name;
        @Nullable
        public final String namespace;
        final int code;

        public UnsupportedElement(@NotNull final String name, @Nullable final String namespace, final int code) {
            this.name = name;
            this.namespace = namespace;
            this.code = code;
        }

        @Override
        public int code() {
            return code;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final UnsupportedElement that = (UnsupportedElement) o;

            if (code != that.code) return false;
            if (!name.equals(that.name)) return false;
            return Objects.equals(namespace, that.namespace);
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + (namespace != null ? namespace.hashCode() : 0);
            result = 31 * result + code;
            return result;
        }
    }

    public static class Error implements TopElement {
        private final int code;
        @NotNull
        public final Type type;
        @NotNull
        public final DefinedCondition definedCondition;

        @NotNull
         private Error(@NotNull final Type type, @NotNull final DefinedCondition definedCondition, final int code) {
            this.type = type;
            this.definedCondition = definedCondition;
            this.code = code;
        }

        public static Error of(@NotNull final Kind kind, @NotNull final Type type, @NotNull final DefinedCondition definedCondition) {
            //TODO: check
            int code = 0;
            switch (kind) {
                case IQ:
                    code = CODE_IQ_ERROR;
                    break;
                case MESSAGE:
                    code = CODE_MESSAGE_ERROR;
                    break;
                case PRESENCE:
                    code = CODE_PRESENCE_ERROR;
                    break;
            }

            return new Error(type, definedCondition, code);
        }

        @Override
        public int code() {
            return code;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final Error error = (Error) o;

            if (code != error.code) return false;
            if (type != error.type) return false;
            return definedCondition == error.definedCondition;
        }

        @Override
        public int hashCode() {
            int result = code;
            result = 31 * result + type.hashCode();
            result = 31 * result + definedCondition.hashCode();
            return result;
        }

        public enum Type {
            AUTH("auth"),
            CANCEL("cancel"),
            CONTINUE("continue"),
            MODIFY("modify"),
            WAIT("wait")
            ;

            @NotNull
            private final String string;

            Type(@NotNull final String string) {
                this.string = string;
            }

            @Override
            public String toString() {
                return string;
            }
        }

        public enum DefinedCondition {
            BAD_REQUEST("bad-request"),
            CONFLICT("conflict"),
            FEATURE_NOT_IMPLEMENTED("feature-not-implemented"),
            ITEM_NOT_FOUND("item-not-found")
            ;

            @NotNull
            private static final String NAMESPACE = "urn:ietf:params:xml:ns:xmpp-stanzas";
            @NotNull
            private final String string;

            DefinedCondition(@NotNull final String string) {
                this.string = string;
            }

            @Override
            public String toString() {
                return string;
            }

            @NotNull
            public String namespace() {
                return NAMESPACE;
            }
        }

        public interface ApplicationSpecificCondition {

        }
    }

    public static final class PresenceEmpty implements TopElement {
        private PresenceEmpty() {}

        public static PresenceEmpty instance() {
            return Holder.OUTER_INSTANCE;
        }

        @Override
        public int code() {
            return CODE_PRESENCE_EMPTY;
        }

        @Override
        public boolean equals(final Object o) {
            return o instanceof PresenceEmpty;
        }

        @Override
        public int hashCode() {
            return PresenceEmpty.class.getName().hashCode();
        }

        private static final class Holder {
            private static final PresenceEmpty OUTER_INSTANCE = new PresenceEmpty();
        }
    }
}
