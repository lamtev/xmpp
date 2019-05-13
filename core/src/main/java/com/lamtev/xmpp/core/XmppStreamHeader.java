package com.lamtev.xmpp.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class XmppStreamHeader implements XmppUnit {
    //Each stream header MUST be qualified by the namespace, so we don't declare it explicitly
    public static final String STREAM_NAMESPACE = "http://etherx.jabber.org/streams";
    @Nullable
    private final String from;
    @Nullable
    private final String to;
    @Nullable
    private final String id;
    private final float version;
    @NotNull
    private final ContentNamespace contentNamespace;

    public XmppStreamHeader(@Nullable String from, @Nullable String to, @Nullable String id, float version, @NotNull ContentNamespace contentNamespace) {
        this.from = from;
        this.to = to;
        this.id = id;
        this.version = version;
        this.contentNamespace = contentNamespace;
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
    public String id() {
        return id;
    }

    public float version() {
        return version;
    }

    @NotNull
    public ContentNamespace contentNamespace() {
        return contentNamespace;
    }

    @Override
    public int code() {
        return CODE_STREAM_HEADER;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        XmppStreamHeader that = (XmppStreamHeader) o;

        if (Float.compare(that.version, version) != 0) return false;
        if (from != null ? !from.equals(that.from) : that.from != null) return false;
        if (to != null ? !to.equals(that.to) : that.to != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return contentNamespace == that.contentNamespace;
    }

    @Override
    public int hashCode() {
        int result = from != null ? from.hashCode() : 0;
        result = 31 * result + (to != null ? to.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (version != +0.0f ? Float.floatToIntBits(version) : 0);
        result = 31 * result + contentNamespace.hashCode();

        return result;
    }

    public enum ContentNamespace {
        CLIENT("jabber:client"),
        SERVER("jabber:server");

        @NotNull
        private final String value;

        ContentNamespace(@NotNull final String value) {
            this.value = value;
        }

        @NotNull
        public static ContentNamespace of(@NotNull final String string) {
            if (CLIENT.value.equals(string)) {
                return CLIENT;
            } else {
                return SERVER;
            }
        }

        public static boolean isContentNamespace(@NotNull final String string) {
            return isJabberClient(string) || isJabberServer(string);
        }

        public static boolean isJabberClient(@NotNull final String string) {
            return CLIENT.value.equals(string);
        }

        public static boolean isJabberServer(@NotNull final String string) {
            return SERVER.value.equals(string);
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
