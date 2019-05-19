package com.lamtev.xmpp.messenger;

import com.lamtev.xmpp.core.*;
import com.lamtev.xmpp.core.XmppStanza.UnsupportedElement;
import com.lamtev.xmpp.core.io.XmppExchange;
import com.lamtev.xmpp.db.DBStorage;
import com.lamtev.xmpp.db.model.Contact;
import com.lamtev.xmpp.db.model.User;
import com.lamtev.xmpp.messenger.utils.AuthBase64LoginPasswordExtractor;
import com.lamtev.xmpp.messenger.utils.StringGenerator;
import com.lamtev.xmpp.server.api.XmppServer;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;

import static com.lamtev.xmpp.core.XmppStanza.Error.DefinedCondition.FEATURE_NOT_IMPLEMENTED;
import static com.lamtev.xmpp.core.XmppStanza.Error.Type.CANCEL;
import static com.lamtev.xmpp.core.XmppStanza.IqQuery.SupportedContentNamespace.ROSTER;
import static com.lamtev.xmpp.core.XmppStanza.Kind.IQ;
import static com.lamtev.xmpp.core.XmppStanza.Kind.PRESENCE;
import static com.lamtev.xmpp.core.XmppStreamFeatures.Type.SASLMechanism.PLAIN;
import static com.lamtev.xmpp.core.util.XmppStanzas.errorOf;
import static com.lamtev.xmpp.core.util.XmppStanzas.rosterResultOf;
import static com.lamtev.xmpp.messenger.utils.StringGenerator.Mode.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;

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
        db.roster().contactsForUserWithJidLocalPart("admin").forEach(it -> {
            System.out.println(it.jidLocalPart);
            System.out.println(it.name);
            System.out.println(it.subscription);
        });

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
                        final var resource = iqBind.resource() != null ? iqBind.resource() : resourceGenerator.nextString();
                        userHandler.setResource(resource);
                        responseStream.sendUnit(new XmppStanza(
                                IQ,
                                st.id(),
                                XmppStanza.TypeAttribute.of(IQ, "result"),
                                new XmppStanza.IqBind(null, userHandler.user().jidLocalPart() + "@lamtev.com/" + resource)
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

                    if (stanza.topElement() instanceof XmppStanza.IqQuery) {
                        final var query = (XmppStanza.IqQuery) stanza.topElement();
                        System.out.println("query!!!");

                        if (query.namespace() == ROSTER) {
                            if (stanza.type() == XmppStanza.IqTypeAttribute.GET) {
                                final var items = db.roster().contactsForUserWithJidLocalPart(userHandler.user().jidLocalPart())
                                        .stream()
                                        .map(it -> new XmppStanza.IqQuery.Item(it.jidLocalPart + "@lamtev.com", it.name, XmppStanza.IqQuery.Item.Subscription.of(it.subscription)))
                                        .collect(toList());
                                responseStream.sendUnit(rosterResultOf(stanza, items));
                            } else if (stanza.type() == XmppStanza.IqTypeAttribute.SET) {
                                System.out.println("Roster set received!");
                                final var elements = query.topElements();
                                if (elements != null && elements.size() == 1) {
                                    final var first = elements.get(0);
                                    if (first instanceof XmppStanza.IqQuery.Item) {
                                        final var item = (XmppStanza.IqQuery.Item) first;
                                        final var success = db.roster().addContactToUserWithJidLocalPart(
                                                userHandler.user().jidLocalPart(),
                                                new Contact(item.jid().replace("@lamtev.com", ""), item.name(), item.subscription() != null ? item.subscription().toString() : null)
                                        );

                                        if (success) {
                                            responseStream.sendUnit(new XmppStanza(
                                                    IQ,
                                                    stanza.from(),
                                                    stanza.to(),
                                                    stanza.id(),
                                                    XmppStanza.TypeAttribute.of(IQ, "result"),
                                                    stanza.lang(),
                                                    new XmppStanza.IqQuery(ROSTER)
                                            ));

                                            sendRosterPushToAllUserResources(userHandler.user(), query);
                                        } else {
                                            System.out.println("Unable to add contact");
                                        }
                                    }
                                }
                            }
                        } else {
                            final var error = errorOf(stanza, CANCEL, FEATURE_NOT_IMPLEMENTED);

                            responseStream.sendUnit(error);
                        }
                    } else if (stanza.topElement() instanceof UnsupportedElement) {
                        final var unsupported = (UnsupportedElement) stanza.topElement();

                        final var response = new XmppStanza(
                                stanza.kind(),
                                stanza.from(),
                                stanza.to(),
                                stanza.id(),
                                XmppStanza.TypeAttribute.of(stanza.kind(), "result"),
                                stanza.lang(),
                                unsupported
                        );

                        System.out.println("Usupported: " + unsupported.name + " " + unsupported.namespace);
                        final var error = errorOf(stanza, CANCEL, FEATURE_NOT_IMPLEMENTED);

                        responseStream.sendUnit(error);
                    } else if (stanza.kind() == PRESENCE) {
                        final var fullJid =  userHandler.user().jidLocalPart() + "@lamtev.com/" + userHandler.resource();
                        responseStream.sendUnit(new XmppStanza(
                                PRESENCE,
                                fullJid,
                                fullJid,
                                null,
                                null,
                                null,
                                XmppStanza.PresenceEmpty.instance()
                        ));
                        responseStream.sendUnit("<message from='lamtev.com' to='anton@lamtev.com' type='chat'>\n" +
                                "                    <subject>Welcome! Добро пожаловать!</subject>\n" +
                                "                    <body>Добро пожаловать на сервер lamtev.com!</body>\n" +
                                "                </message>");
                    }
                }
                break;
        }
    }

    private void sendRosterPushToAllUserResources(@NotNull final User user, @NotNull final XmppStanza.IqQuery query) {
        for (XmppExchange e : userHandlers.keySet()) {
            if (e.jidLocalPart().equals(user.jidLocalPart())) {
                e.responseStream().sendUnit(new XmppStanza(
                        IQ,
                        e.jidLocalPart() + "@lamtev.com/" + e.resource(),
                        null,
                        idGenerator.nextString(),
                        XmppStanza.IqTypeAttribute.SET,
                        null,
                        query
                ));
            }
        }
        System.out.println("Roster push sent to all user resources!");
    }
}
