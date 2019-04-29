package com.lamtev.xmpp.messenger;

import com.lamtev.xmpp.core.*;
import com.lamtev.xmpp.core.io.XMPPExchange;
import com.lamtev.xmpp.messenger.utils.AuthBase64DataExtractor;
import com.lamtev.xmpp.messenger.utils.StringGenerator;
import com.lamtev.xmpp.server.api.XMPPServer;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;

import static com.lamtev.xmpp.core.XmppStreamFeatures.Type.SASLMechanism.PLAIN;
import static java.nio.charset.StandardCharsets.UTF_8;

public class Messenger implements XMPPServer.Handler{
    @NotNull
    private final ConcurrentHashMap<XMPPExchange, User> users = new ConcurrentHashMap<>(10);
    @NotNull
    private final StringGenerator idGenerator = new StringGenerator(64);

    public static void main(String[] args) {
        new Messenger().run();
    }

    private void run() {
        final var server = XMPPServer.of(XMPPServer.Mode.BLOCKING, 12345, Runtime.getRuntime().availableProcessors());
        server.setHandler(this);
        server.start();
    }

    @Override
    public void handle(@NotNull XMPPExchange exchange) {
        final var user = users.computeIfAbsent(exchange, (e) -> new User(idGenerator.nextString()));

        final var initialStream = exchange.initialStream();
        final var responseStream = exchange.responseStream();

        if (initialStream.hasError()) {
            final var error = initialStream.error();
            System.out.println(error);
            //TODO
            return;
        }

        final var unit = initialStream.unit();

        switch (exchange.state()) {
            case INITIAL:
                if (unit instanceof XmppStreamHeader) {
                    final var initialStreamHeader = (XmppStreamHeader) unit;

                    final var streamHeader = new XmppStreamHeader(
                            "lamtev.com",
                            initialStreamHeader.from(),
                            idGenerator.nextString(),
                            initialStreamHeader.version(),
                            initialStreamHeader.contentNamespace()
                    );

                    if (initialStreamHeader.from() != null) {
                        user.setJid(initialStreamHeader.from());
                    }

                    responseStream.open(streamHeader, XmppStreamFeatures.of(PLAIN));
                    System.out.println(PLAIN + " sent");
                }
                break;
            case SASL_NEGOTIATION:
                if (unit instanceof XmppSaslAuth) {

                    final var auth = (XmppSaslAuth) unit;

                    final var jidPass = AuthBase64DataExtractor.extract(auth.body(), UTF_8);
                    if (jidPass == null) {
                        return;
                    }

                    //TODO
                    // if we dont have that user then send auth error

                    user.setJid(jidPass[0] + "@lamtev.com");

                    responseStream.sendUnit(new XmppSaslAuthSuccess());
                    System.out.println("Auth success sent");
                }
                break;
            case RESOURCE_BINDING:
                System.out.println("binding");
                if (unit instanceof XmppStreamHeader) {
                    final var initialStreamHeader = (XmppStreamHeader) unit;

                    final var streamHeader = new XmppStreamHeader(
                            "lamtev.com",
                            initialStreamHeader.from(),
                            idGenerator.nextString(),
                            initialStreamHeader.version(),
                            initialStreamHeader.contentNamespace()
                    );

                    responseStream.open(streamHeader, XmppStreamFeatures.of(XmppStreamFeatures.Type.RESOURCE_BINDING));
                }
        }

    }
}
