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
