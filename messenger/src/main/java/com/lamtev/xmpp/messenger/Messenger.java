package com.lamtev.xmpp.messenger;

import com.lamtev.xmpp.core.XMPPError;
import com.lamtev.xmpp.core.XMPPStanza;
import com.lamtev.xmpp.core.XMPPStreamFeatures;
import com.lamtev.xmpp.core.XMPPStreamHeader;
import com.lamtev.xmpp.messenger.utils.StringGenerator;
import com.lamtev.xmpp.server.api.XMPPServer;
import org.jetbrains.annotations.NotNull;

import static com.lamtev.xmpp.core.XMPPStreamFeatures.Type.SASLMechanism.PLAIN;

public class Messenger {

    @NotNull
    private final StringGenerator stringGenerator = new StringGenerator(64);

    public static void main(String[] args) {
        new Messenger().run();
    }

    private void run() {
        final var server = XMPPServer.of(XMPPServer.Mode.BLOCKING, 12345, Runtime.getRuntime().availableProcessors());
        server.setHandler((exchange) -> {
            final var initialStream = exchange.initialStream();
            final var responseStream = exchange.responseStream();
            if (initialStream.hasError()) {
                final var error = initialStream.error();
                //TODO
                return;
            }

            final var unit = initialStream.unit();
            if (unit instanceof XMPPStreamHeader) {
                final var initialStreamHeader = (XMPPStreamHeader) unit;

                final var streamHeader = new XMPPStreamHeader(
                        "lamtev.com",
                        initialStreamHeader.from(),
                        stringGenerator.nextString(),
                        initialStreamHeader.version(),
                        initialStreamHeader.contentNamespace()
                );

                responseStream.open(streamHeader, XMPPStreamFeatures.of(PLAIN));
            } else if (unit instanceof XMPPStanza) {

            } else if (unit instanceof XMPPError) {

            }
        });
        server.start();
    }
}
