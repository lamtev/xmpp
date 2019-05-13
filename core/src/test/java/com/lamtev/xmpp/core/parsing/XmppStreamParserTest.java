package com.lamtev.xmpp.core.parsing;

import com.lamtev.xmpp.core.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static com.lamtev.xmpp.core.XmppStanza.Kind.IQ;
import static com.lamtev.xmpp.core.XmppStanza.Kind.MESSAGE;
import static com.lamtev.xmpp.core.XmppStreamFeatures.Type.SASLMechanism.PLAIN;
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
                        final var expected = new XmppStreamHeader(
                                "juliet@im.example.com",
                                "im.example.com",
                                null,
                                1.0f,
                                XmppStreamHeader.ContentNamespace.CLIENT
                        );

                        assertEquals(expected, header);
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
                    final var expected = new XmppSaslAuth(PLAIN, "AGFudG9uADEyMzQ1");

                    assertEquals(expected, auth);
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
        final var xml = "<iq id='yhc13a95' type='set'>" +
                "<bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'>" +
                "<resource>balcony</resource>" +
                "</bind>" +
                "</iq>";

        try (var inputStream = new ByteArrayInputStream(xml.getBytes(UTF_16))) {
            final var parser = new XmppStreamParser(inputStream, "UTF-16");

            parser.setDelegate(new XmppStreamParser.Delegate() {
                @Override
                public void parserDidParseUnit(@NotNull XmppUnit unit) {
                    final var resBindingStanza = (XmppStanza) unit;
                    final var expected = new XmppStanza(
                            IQ,
                            "yhc13a95",
                            XmppStanza.TypeAttribute.of(IQ, "set"),
                            new XmppStanza.IqBind("balcony", null)
                    );

                    assertEquals(expected, resBindingStanza);
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
                    final var expected = new XmppStanza(
                            IQ,
                            "yhc13a95",
                            XmppStanza.TypeAttribute.of(IQ, "set"),
                            new XmppStanza.IqBind(null, "juliet@im.example.com/balcony")
                    );

                    assertEquals(expected, resBindingStanza);
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
    void testMessageStanzaParsing() throws IOException, XmppStreamParserException {
        final var xml = "<message from='juliet@im.example.com/balcony' " +
                "id='ju2ba41c' " +
                "to='romeo@example.net' " +
                "type='chat' " +
                "xml:lang='en'> " +
                "<body>Art thou not Romeo, and a Montague?</body>" +
                "</message>";

        try (var inputStream = new ByteArrayInputStream(xml.getBytes(UTF_16))) {
            final var parser = new XmppStreamParser(inputStream, "UTF-16");

            parser.setDelegate(new XmppStreamParser.Delegate() {
                @Override
                public void parserDidParseUnit(@NotNull XmppUnit unit) {
                    final var messageStanza = (XmppStanza) unit;
                    final var expected = new XmppStanza(
                            MESSAGE,
                            "romeo@example.net", "juliet@im.example.com/balcony", "ju2ba41c",
                            XmppStanza.TypeAttribute.of(MESSAGE, "chat"),
                            "en", new XmppStanza.MessageBody("Art thou not Romeo, and a Montague?")
                    );

                    assertEquals(expected, messageStanza);
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
    void testIqStanzaRosterGetParsing() throws IOException, XmppStreamParserException {
        final var xml = "<iq from='juliet@example.com/balcony' " +
                "id='bv1bs71f' " +
                "type='get'>" +
                "<query xmlns='jabber:iq:roster'/>" +
                "</iq>";

        final var expected = new XmppStanza(
                IQ,
                null,
                "juliet@example.com/balcony",
                "bv1bs71f",
                XmppStanza.TypeAttribute.of(IQ, "get"),
                null,
                new XmppStanza.IqQuery(
                        XmppStanza.IqQuery.ContentNamespace.ROSTER,
                        null,
                        null
                )
        );

        try (var inputStream = new ByteArrayInputStream(xml.getBytes(UTF_16))) {
            final var parser = new XmppStreamParser(inputStream, "UTF-16");

            parser.setDelegate(new XmppStreamParser.Delegate() {
                @Override
                public void parserDidParseUnit(@NotNull XmppUnit unit) {
                    final var rosterGet = (XmppStanza) unit;

                    assertEquals(expected, rosterGet);
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
    void testIqStanzaRosterGetParsing2() throws IOException, XmppStreamParserException {
        final var xml = "<iq id=\"b11881b6-e336-4592-9539-d14b7c01caef\" type=\"get\"><query xmlns=\"jabber:iq:roster\"/></iq>";
        final var expected = new XmppStanza(
                IQ,
                "b11881b6-e336-4592-9539-d14b7c01caef",
                XmppStanza.TypeAttribute.of(IQ, "get"),
                new XmppStanza.IqQuery(
                        XmppStanza.IqQuery.ContentNamespace.ROSTER,
                        null,
                        null
                )
        );

        try (var inputStream = new ByteArrayInputStream(xml.getBytes(UTF_16))) {
            final var parser = new XmppStreamParser(inputStream, "UTF-16");

            parser.setDelegate(new XmppStreamParser.Delegate() {
                @Override
                public void parserDidParseUnit(@NotNull XmppUnit unit) {
                    final var rosterGet = (XmppStanza) unit;

                    assertEquals(expected, rosterGet);
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
