package com.lamtev.xmpp.messenger;

import com.lamtev.xmpp.core.*;
import com.lamtev.xmpp.core.XmppStanza.UnsupportedElement;
import com.lamtev.xmpp.core.io.XmppExchange;
import com.lamtev.xmpp.messenger.utils.AuthBase64LoginPasswordExtractor;
import com.lamtev.xmpp.messenger.utils.StringGenerator;
import com.lamtev.xmpp.server.api.XmppServer;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;

import static com.lamtev.xmpp.core.XmppStanza.Error.DefinedCondition.*;
import static com.lamtev.xmpp.core.XmppStanza.Error.Type.CANCEL;
import static com.lamtev.xmpp.core.XmppStanza.Kind.IQ;
import static com.lamtev.xmpp.core.XmppStreamFeatures.Type.SASLMechanism.PLAIN;
import static com.lamtev.xmpp.core.util.XmppStanzas.errorOf;
import static com.lamtev.xmpp.core.util.XmppStanzas.rosterResultOf;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;

public class Messenger implements XmppServer.Handler {
    @NotNull
    private final ConcurrentHashMap<XmppExchange, User> users = new ConcurrentHashMap<>(10);
    @NotNull
    private final StringGenerator idGenerator = new StringGenerator(64);

    public static void main(String[] args) {
        new Messenger().run();
    }

    private void run() {
        final var server = XmppServer.of(XmppServer.Mode.BLOCKING, 12345, Runtime.getRuntime().availableProcessors());
        server.setHandler(this);
        server.start();
    }

    @Override
    public void handle(@NotNull XmppExchange exchange) {
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
            case WAITING_FOR_STREAM_HEADER:
                if (unit instanceof XmppStreamHeader) {
                    System.out.println("Handling " + unit);
                    final var nextState = user.stateQueue.pollFirst();
                    if (nextState == null) {
                        throw new NullPointerException();
                    }

                    switch (nextState) {
                        case SASL_NEGOTIATION: {
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
                        case RESOURCE_BINDING: {
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
                        break;
                        case EXCHANGE: {

                        }
                        break;
                    }
                }
                break;
            case SASL_NEGOTIATION:
                if (unit instanceof XmppSaslAuth) {
                    System.out.println("Handling " + unit);

                    final var auth = (XmppSaslAuth) unit;

                    final var jidPass = AuthBase64LoginPasswordExtractor.extract(auth.body(), UTF_8);
                    if (jidPass == null) {
                        return;
                    }

                    //TODO
                    // if we dont have that user then send auth error

                    user.setJid(jidPass[0] + "@lamtev.com");

                    responseStream.sendUnit(new XmppSaslAuthSuccess());
                    System.out.println("Auth success sent");
                    System.out.println(exchange.state());
                }
                break;
            case RESOURCE_BINDING:
                System.out.println("binding");
                if (unit instanceof XmppStanza) {
                    System.out.println("Handling " + unit);
                    final var st = (XmppStanza) unit;

                    if (st.topElement() instanceof XmppStanza.IqBind) {
                        responseStream.sendUnit(new XmppStanza(
                                IQ,
                                st.id(),
                                XmppStanza.TypeAttribute.of(IQ, "result"),
                                new XmppStanza.IqBind(null, "anton@lamtev.com")
                        ));
                        System.out.println("iq bind result sent");
                    }
                }
                break;
            case EXCHANGE:
                System.out.println("exchange");
                if (unit instanceof XmppStanza) {
                    System.out.println("Handling " + unit);
                    final var stanza = (XmppStanza) unit;

                    if (stanza.type() == XmppStanza.IqTypeAttribute.GET && stanza.topElement() instanceof XmppStanza.IqQuery) {
                        System.out.println("query!!!");

                        final var query = (XmppStanza.IqQuery) stanza.topElement();

                        if (query.namespace() == XmppStanza.IqQuery.ContentNamespace.ROSTER) {
                            responseStream.sendUnit(rosterResultOf(stanza, asList("admin@lamtev.com", "root@lamtev.com")));
                        }
                    } else if (stanza.topElement() instanceof UnsupportedElement) {
                        final var unsupported = (UnsupportedElement) stanza.topElement();
                        System.out.println("Usupported: " + unsupported.name);
                        final var error = errorOf(stanza, CANCEL, ITEM_NOT_FOUND);

                        responseStream.sendUnit(error);
                    }
                }
                break;
        }
    }
}
