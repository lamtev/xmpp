package com.lamtev.xmpp.core.parsing;

import com.lamtev.xmpp.core.XmppUnit;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamReader;

interface XmppStreamParserStrategy {
    static boolean isPotentialStreamHeader(@NotNull final String element) {
        return "stream".equals(element);
    }

    static boolean isPotentialStreamFeatures(@NotNull final String element) {
        return "features".equals(element);
    }

    static boolean isPotentialSaslNegotiation(@NotNull final String element) {
        return "auth".equals(element);
    }

    //TODO: replace with set
    static boolean isPotentialStanzaIq(@NotNull final String element) {
        return "iq".equals(element);
    }

    static boolean isPotentialStanzaMessage(@NotNull final String element) {
        return "message".equals(element);
    }

    static boolean isPotentialStanzaPresence(@NotNull final String element) {
        return "presence".equals(element);
    }

    static boolean isPotentialError(@NotNull final String element) {
        return "error".equals(element);
    }

    void startElementReached(@NotNull final String name);

    void endElementReached();

    void charactersReached();

    boolean unitIsReady();

    @NotNull
    XmppUnit readyUnit();

    void setErrorObserver(@NotNull final ErrorObserver observer);

    void updateReader(@NotNull final XMLStreamReader reader);

//    /**
//     * Unique sequential code associated with concrete XmppStreamParserStrategy instance
//     * @see XmppUnit#code()
//     * @return
//     */
//    int code();

    enum Name {
        STREAM_HEADER,
        STREAM_FEATURES,
        SASL_NEGOTIATION,
        STANZA_IQ,
        STANZA_MESSAGE,
        STANZA_PRESENCE,
        ERROR,
    }

    interface ErrorObserver {
        void onError(@NotNull final XmppStreamParser.Error error);
    }
}
