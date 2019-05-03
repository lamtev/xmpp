package com.lamtev.xmpp.core.io;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

final class XmppInputStreamTest {
    @Test
    void test() throws Throwable {
        final var xml = "<?xml version='1.0' encoding='UTF-16'?>" +
                "<stream:stream\n" +
                "       from='juliet@im.example.com'\n" +
                "       to='im.example.com'\n" +
                "       version='1.0'\n" +
                "       xml:lang='en'\n" +
                "       xmlns='jabber:client'\n" +
                "       xmlns:stream='http://etherx.jabber.org/streams'/>";
        try (final var stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_16))) {
            final var initialStream = new XmppInputStream(stream, "UTF-16");
            initialStream.setHandler(() -> {
                if (initialStream.hasError()) {
                    System.out.println(initialStream.error());
                } else {
                    System.out.println(initialStream.unit());
                }
            });
            initialStream.open();
        }
    }
}