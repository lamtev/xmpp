package com.lamtev.xmpp.core.io;

import com.lamtev.xmpp.core.XmppStreamFeatures;
import com.lamtev.xmpp.core.XmppStreamFeatures.Type.SASLMechanism;
import com.lamtev.xmpp.core.XmppStreamHeader;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

class XmppOutputStreamTest {
    @NotNull
    private static final XmppStreamHeader STREAM_HEADER = new XmppStreamHeader(
            "lamtev.com",
            null,
            "jk234hjha#$KJkj1234kKJ#@$j",
            1.0f,
            XmppStreamHeader.ContentNamespace.CLIENT
    );
    @NotNull
    private static final String STREAM_HEADER_STRING = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<stream:stream " +
            "from=\"lamtev.com\" " +
            "id=\"jk234hjha#$KJkj1234kKJ#@$j\" " +
            "version=\"1.0\" " +
            "xml:lang=\"en\" " +
            "xmlns=\"jabber:client\" " +
            "xmlns:stream=\"http://etherx.jabber.org/streams\">";
    @NotNull
    private static final XmppStreamFeatures FEATURES_BIND = XmppStreamFeatures.of(XmppStreamFeatures.Type.RESOURCE_BINDING);
    @NotNull
    private static final String FEATURES_BIND_STRING = "<stream:features>" +
            "<bind xmlns=\"urn:ietf:params:xml:ns:xmpp-bind\"/>" +
            "</stream:features>";
    @NotNull
    private final ByteArrayOutputStream os = new ByteArrayOutputStream();
    @NotNull
    private final XmppOutputStream stream = new XmppOutputStream(os, "UTF-8");

    @Test
    void testOpenWithHeaderAndFeaturesSASL() {
        final var expected = STREAM_HEADER_STRING +
                "<stream:features>" +
                "<mechanisms xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">" +
                "<mechanism>PLAIN</mechanism>" +
                "</mechanisms>" +
                "</stream:features>";

        stream.open(STREAM_HEADER, XmppStreamFeatures.of(SASLMechanism.PLAIN));

        assertEquals(expected, os.toString(UTF_8));
    }

    @Test
    void testOpenWithHeaderAndFeaturesResourceBinding() {
        final var expected = STREAM_HEADER_STRING + FEATURES_BIND_STRING;

        stream.open(STREAM_HEADER, XmppStreamFeatures.of(XmppStreamFeatures.Type.RESOURCE_BINDING));

        assertEquals(expected, os.toString(UTF_8));
    }

    @Test
    void testSendSingleHeader() {
        stream.sendUnit(STREAM_HEADER);

        assertEquals(STREAM_HEADER_STRING, os.toString(UTF_8));
    }


    @Test
    void testSendSingleFeaturesBind() {
        stream.sendUnit(FEATURES_BIND);

        assertEquals(FEATURES_BIND_STRING, os.toString(UTF_8));
    }
}
