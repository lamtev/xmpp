package com.lamtev.xmpp.core.parsing;

import com.lamtev.xmpp.core.XmppUnit;
import org.jetbrains.annotations.NotNull;

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
    static boolean isPotentialStanza(@NotNull final String element) {
        return "message".equals(element) || "presence".equals(element) || "iq".equals(element) || "bind".equals(element) || "resource".equals(element) || "jid".equals(element);
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
        STANZA,
        ERROR,
    }

    interface ErrorObserver {
        void onError(@NotNull final XmppStreamParser.Error error);
    }

}
