package com.lamtev.xmpp.core.parsing;

import com.lamtev.xmpp.core.XMPPStreamCloseTag;
import com.lamtev.xmpp.core.XMPPStreamHeader;
import com.lamtev.xmpp.core.XMPPUnit;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static java.nio.charset.StandardCharsets.UTF_16;
import static org.junit.jupiter.api.Assertions.*;

class XMPPStreamParserTest {
    @Test
    void testValidStreamHeaderParsing() {
        String xml = "<?xml version='1.0' encoding='UTF-16'?>" +
                "<stream:stream\n" +
                "       from='juliet@im.example.com'\n" +
                "       to='im.example.com'\n" +
                "       version='1.0'\n" +
                "       xml:lang='en'\n" +
                "       xmlns='jabber:client'\n" +
                "       xmlns:stream='http://etherx.jabber.org/streams'/>";
        final var parser = new XMPPStreamParser(new ByteArrayInputStream(xml.getBytes(UTF_16)), "UTF-16");
        parser.setDelegate(new XMPPStreamParser.Delegate() {
            private int parserDidParseUnitCallCount = 0;

            @Override
            public void parserDidParseUnit(@NotNull final XMPPUnit unit) {
                if (parserDidParseUnitCallCount == 0) {
                    assertTrue(unit instanceof XMPPStreamHeader);

                    final var header = (XMPPStreamHeader) unit;

                    assertEquals("juliet@im.example.com", header.from());
                    assertEquals("im.example.com", header.to());
                    assertNull(header.id());
                    assertEquals(1.0, header.version());
                    assertSame(XMPPStreamHeader.ContentNamespace.CLIENT, header.contentNamespace());
                    assertEquals("jabber:client", header.contentNamespace().toString());
                } else if (parserDidParseUnitCallCount == 1) {
                    assertTrue(unit instanceof XMPPStreamCloseTag);
                } else {
                    fail("In that case there must be exactly 2 calls of parserDidParseUnit()");
                }

                parserDidParseUnitCallCount++;
            }

            @Override
            public void parserDidFailWithError(@NotNull final XMPPStreamParser.Error error) {}
        });
        parser.startParsing();
    }
}
