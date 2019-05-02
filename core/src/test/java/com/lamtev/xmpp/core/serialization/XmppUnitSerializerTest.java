package com.lamtev.xmpp.core.serialization;

import com.lamtev.xmpp.core.XmppSaslAuthSuccess;
import com.lamtev.xmpp.core.XmppStreamFeatures;
import com.lamtev.xmpp.core.XmppStreamHeader;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class XmppUnitSerializerTest {
    @NotNull
    private static final XmppUnitSerializer serializer = new XmppUnitSerializer("UTF-8");

    @Test
    void testStreamHeaderSerialization() throws IOException {
        final var expectedStreamHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<stream:stream " +
                "from=\"juliet@im.example.com\" " +
                "to=\"im.example.com\" " +
                "version=\"1.0\" " +
                "xml:lang=\"en\" " +
                "xmlns=\"jabber:client\" " +
                "xmlns:stream=\"http://etherx.jabber.org/streams\">";

        final var streamHeader = new XmppStreamHeader("juliet@im.example.com", "im.example.com", null, 1.0f, XmppStreamHeader.ContentNamespace.CLIENT);

        try (var baos = new ByteArrayOutputStream()) {
            baos.writeBytes(serializer.serialize(streamHeader));

            assertEquals(expectedStreamHeader, baos.toString(UTF_8));
        }
    }

    @Test
    void testStreamFeaturesSASLSerialization() throws IOException {
        final var expectedStreamFeatures = "<stream:features>" +
                "<mechanisms xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">" +
                "<mechanism>PLAIN</mechanism>" +
                "</mechanisms>" +
                "</stream:features>";

        final var streamFeatures = XmppStreamFeatures.of(XmppStreamFeatures.Type.SASLMechanism.PLAIN);

        try (var baos = new ByteArrayOutputStream()) {
            baos.writeBytes(serializer.serialize(streamFeatures));

            assertEquals(expectedStreamFeatures, baos.toString(UTF_8));
        }
    }

    @Test
    void testStreamFeaturesResourceBindingSerialization() throws IOException {
        final var expectedStreamFeatures = "<stream:features>" +
                "<bind xmlns=\"urn:ietf:params:xml:ns:xmpp-bind\"/>" +
                "</stream:features>";

        final var streamFeatures = XmppStreamFeatures.of(XmppStreamFeatures.Type.RESOURCE_BINDING);

        try (var baos = new ByteArrayOutputStream()) {
            baos.writeBytes(serializer.serialize(streamFeatures));

            assertEquals(expectedStreamFeatures, baos.toString(UTF_8));
        }
    }

    @Test
    void testSaslAuthSuccessSerialization() throws IOException {
        final var expectedSuccess = "<success xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\"></success>";

        try (var baos = new ByteArrayOutputStream()) {
            baos.writeBytes(serializer.serialize(new XmppSaslAuthSuccess()));

            assertEquals(expectedSuccess, baos.toString(UTF_8));
        }
    }
}
