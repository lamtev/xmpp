package com.lamtev.xmpp.core.parsing;

import com.lamtev.xmpp.core.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static com.lamtev.xmpp.core.XmppStanza.Kind.IQ;
import static java.nio.charset.StandardCharsets.UTF_16;
import static org.junit.jupiter.api.Assertions.*;

final class XmppStreamParserTest {
    @Test
    void testValidStreamHeaderParsing() throws XmppStreamParserException, IOException {
        final var xml = "<?xml version='1.0' encoding='UTF-16'?>" +
                "<stream:stream\n" +
                "       from='juliet@im.example.com'\n" +
                "       to='im.example.com'\n" +
                "       version='1.0'\n" +
                "       xml:lang='en'\n" +
                "       xmlns='jabber:client'\n" +
                "       xmlns:stream='http://etherx.jabber.org/streams'>";
        try (final var inputStream = new ByteArrayInputStream(xml.getBytes(UTF_16))) {
            final var parser = new XmppStreamParser(inputStream, "UTF-16");
            parser.setDelegate(new XmppStreamParser.Delegate() {
                private int parserDidParseUnitCallCount = 0;

                @Override
                public void parserDidParseUnit(@NotNull final XmppUnit unit) {
                    if (parserDidParseUnitCallCount == 0) {
                        assertTrue(unit instanceof XmppStreamHeader);

                        final var header = (XmppStreamHeader) unit;

                        assertEquals("juliet@im.example.com", header.from());
                        assertEquals("im.example.com", header.to());
                        assertNull(header.id());
                        assertEquals(1.0, header.version());
                        assertSame(XmppStreamHeader.ContentNamespace.CLIENT, header.contentNamespace());
                        assertEquals("jabber:client", header.contentNamespace().toString());
                    } else if (parserDidParseUnitCallCount == 1) {
                        assertTrue(unit instanceof XmppStreamCloseTag);
                    } else {
                        fail("In that case there must be exactly 2 calls of parserDidParseUnit()");
                    }

                    parserDidParseUnitCallCount++;
                }

                @Override
                public void parserDidFailWithError(@NotNull final XmppStreamParser.Error error) {
                }
            });
            parser.startParsing();
        }
    }

    @Test
    void testSaslAuthParsing() throws IOException, XmppStreamParserException {
        final var xml = "<auth xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\"\n" +
                "mechanism=\"PLAIN\">AGFudG9uADEyMzQ1</auth>";

        try (var inputStream = new ByteArrayInputStream(xml.getBytes(UTF_16))) {
            final var parser = new XmppStreamParser(inputStream, "UTF-16");

            parser.setDelegate(new XmppStreamParser.Delegate() {
                @Override
                public void parserDidParseUnit(@NotNull XmppUnit unit) {
                    final var auth = (XmppSaslAuth) unit;

                    assertEquals("AGFudG9uADEyMzQ1", auth.body());
                    assertEquals(XmppStreamFeatures.Type.SASLMechanism.PLAIN, auth.mechanism());
                }

                @Override
                public void parserDidFailWithError(XmppStreamParser.@NotNull Error error) {
                    fail("Unexpected error: " + error);
                }
            });

            parser.startParsing();
        }
    }

    @Test
    void testResourceBindingIqBindResourceParsing() throws XmppStreamParserException, IOException {
        final var xml = "<iq id='yhc13a95' type='set'>\n" +
                "     <bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'>\n" +
                "       <resource>balcony</resource>\n" +
                "     </bind>\n" +
                "   </iq>";

        try (var inputStream = new ByteArrayInputStream(xml.getBytes(UTF_16))) {
            final var parser = new XmppStreamParser(inputStream, "UTF-16");

            parser.setDelegate(new XmppStreamParser.Delegate() {
                @Override
                public void parserDidParseUnit(@NotNull XmppUnit unit) {
                    final var resBindingStanza = (XmppStanza) unit;

                    assertEquals(IQ, resBindingStanza.kind());
                    assertEquals("yhc13a95", resBindingStanza.id());
                    assertSame(XmppStanza.TypeAttribute.of(IQ, "set"), resBindingStanza.type());
                    assertEquals("balcony", ((XmppStanza.IqBind) resBindingStanza.entry()).resource());
                }

                @Override
                public void parserDidFailWithError(XmppStreamParser.@NotNull Error error) {
                    fail("Unexpected error: " + error);
                }
            });

            parser.startParsing();
        }
    }

    @Test
    void testResourceBindingIqBindJidParsing() throws IOException, XmppStreamParserException {
        final var xml = "<iq id=\"yhc13a95\" type=\"set\">" +
                "<bind xmlns=\"urn:ietf:params:xml:ns:xmpp-bind\">" +
                "<jid>juliet@im.example.com/balcony</jid>" +
                "</bind>" +
                "</iq>";

        try (var inputStream = new ByteArrayInputStream(xml.getBytes(UTF_16))) {
            final var parser = new XmppStreamParser(inputStream, "UTF-16");

            parser.setDelegate(new XmppStreamParser.Delegate() {
                @Override
                public void parserDidParseUnit(@NotNull XmppUnit unit) {
                    final var resBindingStanza = (XmppStanza) unit;

                    assertEquals(IQ, resBindingStanza.kind());
                    assertEquals("yhc13a95", resBindingStanza.id());
                    assertSame(XmppStanza.TypeAttribute.of(IQ, "set"), resBindingStanza.type());
                    assertEquals("juliet@im.example.com/balcony", ((XmppStanza.IqBind) resBindingStanza.entry()).jid());
                }

                @Override
                public void parserDidFailWithError(XmppStreamParser.@NotNull Error error) {
                    fail("Unexpected error: " + error);
                }
            });

            parser.startParsing();
        }
    }
}
