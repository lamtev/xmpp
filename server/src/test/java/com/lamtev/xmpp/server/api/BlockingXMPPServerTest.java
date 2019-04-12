package com.lamtev.xmpp.server.api;

import com.lamtev.xmpp.core.XMPPError;
import com.lamtev.xmpp.core.XMPPStanza;
import com.lamtev.xmpp.core.XMPPStreamHeader;
import org.junit.jupiter.api.Test;

import static java.time.Duration.ofSeconds;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

class BlockingXMPPServerTest {
    @Test
    void test() {
        assertTimeoutPreemptively(ofSeconds(5), () -> {
            final var server = XMPPServer.of(XMPPServer.Mode.BLOCKING, 12345, Runtime.getRuntime().availableProcessors());
            final var t = new Thread(() -> {
                server.setHandler((initialStream, responseStream) -> {
                    if (initialStream.hasError()) {
                        final var error = initialStream.error();
                        //process error
                        return;
                    }

                    final var unit = initialStream.unit();
                    if (unit instanceof XMPPStreamHeader) {
                        final var streamHeader = (XMPPStreamHeader) unit;
                    } else if (unit instanceof XMPPStanza) {
                        final var stanza = (XMPPStanza) unit;
                    } else if (unit instanceof XMPPError) {
                        final var error = (XMPPError) unit;
                    }
                });
                server.start();
            });
            t.start();

            Thread.sleep(3000L);

            //currently does nothing
            server.stop();

            t.join();
        });
    }
}
