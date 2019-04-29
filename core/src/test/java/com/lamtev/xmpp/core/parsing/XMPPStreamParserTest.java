package com.lamtev.xmpp.core.parsing;

import com.lamtev.xmpp.core.XmppStreamCloseTag;
import com.lamtev.xmpp.core.XmppStreamHeader;
import com.lamtev.xmpp.core.XmppUnit;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static java.nio.charset.StandardCharsets.UTF_16;
import static org.junit.jupiter.api.Assertions.*;

class XMPPStreamParserTest {
    @Test
    void testValidStreamHeaderParsing() throws XMPPStreamParserException {
        final var xml = "<?xml version='1.0' encoding='UTF-16'?>" +
                "<stream:stream\n" +
                "       from='juliet@im.example.com'\n" +
                "       to='im.example.com'\n" +
                "       version='1.0'\n" +
                "       xml:lang='en'\n" +
                "       xmlns='jabber:client'\n" +
                "       xmlns:stream='http://etherx.jabber.org/streams'>";
        final var parser = new XMPPStreamParser(new ByteArrayInputStream(xml.getBytes(UTF_16)), "UTF-16");
        parser.setDelegate(new XMPPStreamParser.Delegate() {
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
            public void parserDidFailWithError(@NotNull final XMPPStreamParser.Error error) {
            }
        });
        parser.startParsing();
    }
}
