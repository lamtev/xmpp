package com.lamtev.xmpp.core.serialization;

import com.lamtev.xmpp.core.XmppSaslAuthSuccess;
import com.lamtev.xmpp.core.XmppStanza;
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

        try (final var baos = new ByteArrayOutputStream()) {
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

        try (final var baos = new ByteArrayOutputStream()) {
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

        try (final var baos = new ByteArrayOutputStream()) {
            baos.writeBytes(serializer.serialize(streamFeatures));

            assertEquals(expectedStreamFeatures, baos.toString(UTF_8));
        }
    }

    @Test
    void testSaslAuthSuccessSerialization() throws IOException {
        final var expectedSuccess = "<success xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\"></success>";

        try (final var baos = new ByteArrayOutputStream()) {
            baos.writeBytes(serializer.serialize(new XmppSaslAuthSuccess()));

            assertEquals(expectedSuccess, baos.toString(UTF_8));
        }
    }

    @Test
    void testIqStanzaBindResourceSerialization() throws IOException {
        final var expectedIqStanza = "<iq id=\"yhc13a95\" type=\"set\">" +
                "<bind xmlns=\"urn:ietf:params:xml:ns:xmpp-bind\">" +
                "<resource>balcony</resource>" +
                "</bind>" +
                "</iq>";

        try (final var baos = new ByteArrayOutputStream()) {
            baos.writeBytes(serializer.serialize(new XmppStanza(
                    XmppStanza.Kind.IQ,
                    "yhc13a95",
                    XmppStanza.IqTypeAttribute.SET,
                    new XmppStanza.IqStanzaBind("balcony", null)
            )));

            assertEquals(expectedIqStanza, baos.toString(UTF_8));
        }
    }

    @Test
    void testIqStanzaBindJidSerialization() throws IOException {
        final var expectedIqStanza = "<iq id=\"yhc13a95\" type=\"set\">" +
                "<bind xmlns=\"urn:ietf:params:xml:ns:xmpp-bind\">" +
                "<jid>juliet@im.example.com/balcony</jid>" +
                "</bind>" +
                "</iq>";

        try (final var baos = new ByteArrayOutputStream()) {
            baos.writeBytes(serializer.serialize(new XmppStanza(
                    XmppStanza.Kind.IQ,
                    "yhc13a95",
                    XmppStanza.IqTypeAttribute.SET,
                    new XmppStanza.IqStanzaBind(null, "juliet@im.example.com/balcony")
            )));

            assertEquals(expectedIqStanza, baos.toString(UTF_8));
        }
    }
}
