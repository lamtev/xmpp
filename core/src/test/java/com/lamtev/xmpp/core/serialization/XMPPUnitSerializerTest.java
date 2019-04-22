package com.lamtev.xmpp.core.serialization;

import com.lamtev.xmpp.core.XMPPStreamFeatures;
import com.lamtev.xmpp.core.XMPPStreamHeader;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

class XMPPUnitSerializerTest {
    @NotNull
    private static final XMPPUnitSerializer serializer = new XMPPUnitSerializer("UTF-8");

    @Test
    void testStreamHeaderSerialization() {
        final var expectedStreamHeader = "<stream:stream " +
                "from=\"juliet@im.example.com\" " +
                "to=\"im.example.com\" " +
                "version=\"1.0\" " +
                "xml:lang=\"en\" " +
                "xmlns=\"jabber:client\" " +
                "xmlns:stream=\"http://etherx.jabber.org/streams\">";

        final var streamHeader = new XMPPStreamHeader("juliet@im.example.com", "im.example.com", null, 1.0f, XMPPStreamHeader.ContentNamespace.CLIENT);

        final var baos = new ByteArrayOutputStream();
        baos.writeBytes(serializer.serialize(streamHeader));

        assertEquals(expectedStreamHeader, baos.toString(UTF_8));
    }

    @Test
    void testStreamFeaturesSASLSerialization() {
        final var expectedStreamFeatures = "<stream:features>" +
                "<mechanisms xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">" +
                "<mechanism>PLAIN</mechanism>" +
                "</mechanisms>" +
                "</stream:features>";

        final var streamFeatures = XMPPStreamFeatures.of(XMPPStreamFeatures.SASLMechanism.PLAIN);

        final var baos = new ByteArrayOutputStream();
        baos.writeBytes(serializer.serialize(streamFeatures));

        assertEquals(expectedStreamFeatures, baos.toString(UTF_8));
    }

    @Test
    void testStreamFeaturesResourceBindingSerialization() {
        final var expectedStreamFeatures = "<stream:features>" +
                "<bind xmlns=\"urn:ietf:params:xml:ns:xmpp-bind\"/>" +
                "</stream:features>";

        final var streamFeatures = XMPPStreamFeatures.of(XMPPStreamFeatures.Type.RESOURCE_BINDING);

        final var baos = new ByteArrayOutputStream();
        baos.writeBytes(serializer.serialize(streamFeatures));

        assertEquals(expectedStreamFeatures, baos.toString(UTF_8));
    }
}