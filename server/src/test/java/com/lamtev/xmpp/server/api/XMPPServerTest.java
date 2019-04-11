package com.lamtev.xmpp.server.api;

import com.lamtev.xmpp.core.io.XMPPInputStream;
import com.lamtev.xmpp.core.parsing.XMPPStreamParser;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;


class XMPPServerTest {

    @Test
    void test() throws Throwable {
//        final var server = XMPPServer.of(XMPPServer.Mode.BLOCKING, 12345, Runtime.getRuntime().availableProcessors());
//        server.setHandler((initialStream, responseStream) -> {
//            if (initialStream.hasError()) {
//                final var error = initialStream.error();
//                //process error
//                return;
//            }
//
//            final var unit = initialStream.unit();
//            if (unit instanceof XMPPStreamHeader) {
//                final var streamHeader = (XMPPStreamHeader) unit;
//            } else if (unit instanceof XMPPStanza) {
//                final var stanza = (XMPPStanza) unit;
//            } else if (unit instanceof XMPPError) {
//                final var error = (XMPPError) unit;
//            }
//        });
//        server.start();

        String xml = "<?xml version=\"1.0\"?>" +
                "<stream:stream\n" +
                "       from='juliet@im.example.com'\n" +
                "       to='im.example.com'\n" +
                "       version='1.0'\n" +
                "       xml:lang='en'\n" +
                "       xmlns='jabber:client'\n" +
                "       xmlns:stream='http://etherx.jabber.org/streams'/>";
        ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_16));

        XMPPInputStream initialStream = new XMPPInputStream(stream, "UTF-16");
        initialStream.open(() -> {
            if (initialStream.hasError()) {
                System.out.println(initialStream.error());
            } else {
                System.out.println(initialStream.unit());
            }
        });

//        XMPPStreamParser p = new XMPPStreamParser(stream, "UTF-16");
//        p.startParsing();
        assertEquals("", "");
    }

}
