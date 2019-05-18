package com.lamtev.xmpp.messenger;

import com.lamtev.xmpp.core.*;
import com.lamtev.xmpp.core.XmppStanza.IqQuery.Item;
import com.lamtev.xmpp.core.XmppStanza.UnsupportedElement;
import com.lamtev.xmpp.core.io.XmppExchange;
import com.lamtev.xmpp.db.DBStorage;
import com.lamtev.xmpp.messenger.utils.AuthBase64LoginPasswordExtractor;
import com.lamtev.xmpp.messenger.utils.StringGenerator;
import com.lamtev.xmpp.server.api.XmppServer;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.lamtev.xmpp.core.XmppStanza.Error.DefinedCondition.ITEM_NOT_FOUND;
import static com.lamtev.xmpp.core.XmppStanza.Error.Type.CANCEL;
import static com.lamtev.xmpp.core.XmppStanza.IqQuery.Item.Subscription.BOTH;
import static com.lamtev.xmpp.core.XmppStanza.IqQuery.Item.Subscription.TO;
import static com.lamtev.xmpp.core.XmppStanza.Kind.IQ;
import static com.lamtev.xmpp.core.XmppStreamFeatures.Type.SASLMechanism.PLAIN;
import static com.lamtev.xmpp.core.util.XmppStanzas.errorOf;
import static com.lamtev.xmpp.core.util.XmppStanzas.rosterResultOf;
import static com.lamtev.xmpp.messenger.utils.StringGenerator.Mode.*;
import static java.nio.charset.StandardCharsets.UTF_8;

public class Messenger implements XmppServer.Handler {
    private final int port;
    @NotNull
    private final DBStorage db;
    @NotNull
    private final ConcurrentHashMap<XmppExchange, UserHandler> userHandlers = new ConcurrentHashMap<>(10);
    @NotNull
    private final StringGenerator idGenerator = new StringGenerator(LETTERS | DIGITS | SPECIAL_SYMBOLS, 64);
    @NotNull
    private final StringGenerator resourceGenerator = new StringGenerator(LETTERS | DIGITS, 32);

    private Messenger(@NotNull final Config config) throws Exception {
        final var dbConfig = config.getConfig("messenger.db");

        port = config.getInt("messenger.port");
        db = new DBStorage(dbConfig);
    }

    public static void main(String[] args) {
        try {
            final var config = ConfigFactory.load("Messenger.conf");
            new Messenger(config).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void run() {
        System.out.println(db.users().isPresent("anton", "Secret_Pass"));

        final var server = XmppServer.of(XmppServer.Mode.BLOCKING, port, Runtime.getRuntime().availableProcessors());
        server.setHandler(this);
        server.start();
    }

    @Override
    public void handle(@NotNull XmppExchange exchange) {
        final var userHandler = userHandlers.computeIfAbsent(exchange, (e) -> new UserHandler());

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
                    final var nextState = userHandler.stateQueue.pollFirst();
                    if (nextState == null) {
                        throw null;
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
                    final var jidLocalPart = jidPass[0].replace("@lamtev.com", "");
                    final var password = jidPass[1];

                    System.out.println(jidLocalPart);
                    System.out.println(password);

                    final var user = db.users().userForJidLocalPart(jidLocalPart, password);
                    if (user != null) {
                        userHandler.setUser(user);
                        responseStream.sendUnit(XmppSaslAuthSuccess.instance());
                        System.out.println("Auth success sent");
                        System.out.println(exchange.state());
                    } else {
                        responseStream.addUnitToBatch(XmppSaslAuthFailure.instance());
                        responseStream.addUnitToBatch(XmppStreamCloseTag.instance());
                        responseStream.sendWholeBatch();
                        System.out.println("Auth failure sent");
                    }
                }
                break;
            case RESOURCE_BINDING:
                System.out.println("binding");
                if (unit instanceof XmppStanza) {
                    System.out.println("Handling " + unit);
                    final var st = (XmppStanza) unit;

                    if (st.topElement() instanceof XmppStanza.IqBind) {
                        final var iqBind = (XmppStanza.IqBind) st.topElement();
                        final var resource = iqBind.resource();
                        responseStream.sendUnit(new XmppStanza(
                                IQ,
                                st.id(),
                                XmppStanza.TypeAttribute.of(IQ, "result"),
                                new XmppStanza.IqBind(null, "anton@lamtev.com/" + (resource != null ? resource : resourceGenerator.nextString()))
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
                            responseStream.sendUnit(rosterResultOf(stanza, List.of(
                                    new Item("admin@lamtev.com", "Admin", BOTH),
                                    new Item("root@lamtev.com", "Root", TO)
                            )));
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
