package com.lamtev.xmpp.core.io;

import com.lamtev.xmpp.core.XMPPStreamFeatures;
import com.lamtev.xmpp.core.XMPPStreamFeatures.Type.SASLMechanism;
import com.lamtev.xmpp.core.XMPPStreamHeader;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

class XMPPOutputStreamTest {
    @NotNull
    private static final XMPPStreamHeader streamHeader = new XMPPStreamHeader(
            "lamtev.com",
            null,
            "jk234hjha#$KJkj1234kKJ#@$j",
            1.0f,
            XMPPStreamHeader.ContentNamespace.CLIENT
    );
    @NotNull
    private static final String STREAM_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<stream:stream " +
            "from=\"lamtev.com\" " +
            "id=\"jk234hjha#$KJkj1234kKJ#@$j\" " +
            "version=\"1.0\" " +
            "xml:lang=\"en\" " +
            "xmlns=\"jabber:client\" " +
            "xmlns:stream=\"http://etherx.jabber.org/streams\">";
    @NotNull
    private final ByteArrayOutputStream os = new ByteArrayOutputStream();
    @NotNull
    private final XMPPOutputStream stream = new XMPPOutputStream(os, "UTF-8");

    @Test
    void testOpenWithHeaderAndFeaturesSASL() {
        final var expected = STREAM_HEADER +
                "<stream:features>" +
                "<mechanisms xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">" +
                "<mechanism>PLAIN</mechanism>" +
                "</mechanisms>" +
                "</stream:features>";

        stream.open(streamHeader, XMPPStreamFeatures.of(SASLMechanism.PLAIN));

        assertEquals(expected, os.toString(UTF_8));
    }

    @Test
    void testOpenWithHeaderAndFeaturesResourceBinding() {
        final var expected = STREAM_HEADER +
                "<stream:features>" +
                "<bind xmlns=\"urn:ietf:params:xml:ns:xmpp-bind\"/>" +
                "</stream:features>";

        stream.open(streamHeader, XMPPStreamFeatures.of(XMPPStreamFeatures.Type.RESOURCE_BINDING));

        assertEquals(expected, os.toString(UTF_8));
    }
}
