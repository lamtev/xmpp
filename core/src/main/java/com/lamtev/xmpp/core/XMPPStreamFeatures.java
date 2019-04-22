package com.lamtev.xmpp.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public final class XMPPStreamFeatures implements XMPPUnit {
    @NotNull
    private final Type type;
    @Nullable
    private SASLMechanism[] mechanisms;

    private XMPPStreamFeatures(@NotNull final Type type) {
        this.type = type;
    }

    public static XMPPStreamFeatures of(@NotNull final Type type) {
        return new XMPPStreamFeatures(type);
    }

    public static XMPPStreamFeatures of(@NotNull final SASLMechanism... mechanisms) {
        final var features = new XMPPStreamFeatures(Type.SASL);
        features.setMechanisms(mechanisms);

        return features;
    }

    @NotNull
    public Type type() {
        return type;
    }

    @Override
    public int code() {
        return CODE_STREAM_FEATURES;
    }

    @NotNull
    public SASLMechanism[] mechanisms() {
        if (mechanisms == null) {
            throw new IllegalStateException();
        }

        return mechanisms;
    }

    private void setMechanisms(@NotNull final SASLMechanism... mechanisms) {
        this.mechanisms = mechanisms;
    }

    public enum Type {
        TLS("urn:ietf:params:xml:ns:xmpp-tls"),
        SASL("urn:ietf:params:xml:ns:xmpp-sasl"),
        RESOURCE_BINDING("urn:ietf:params:xml:ns:xmpp-bind"),
        ;

        @NotNull
        private final String namespace;

        Type(@NotNull final String namespace) {
            this.namespace = namespace;
        }

        @Override
        public String toString() {
            return namespace;
        }
    }

    public enum SASLMechanism {
        PLAIN("PLAIN");

        @NotNull
        private final String value;

        SASLMechanism(@NotNull final String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}