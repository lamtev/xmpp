package com.lamtev.xmpp.core.parsing;

import com.lamtev.xmpp.core.XmppUnit;
import org.jetbrains.annotations.NotNull;

interface XMPPStreamParserStrategy {

    static boolean isPotentialStreamHeader(@NotNull final String element) {
        return "stream".equals(element);
    }

    static boolean isPotentialStreamFeatures(@NotNull final String element) {
        return "features".equals(element);
    }

    static boolean isPotentialSASLNegotiation(@NotNull final String element) {
        return "auth".equals(element);
    }

    static boolean isPotentialStanza(@NotNull final String element) {
        return "message".equals(element) || "presence".equals(element) || "iq".equals(element);
    }

    static boolean isPotentialError(@NotNull final String element) {
        return "error".equals(element);
    }

    void startElementReached();

    void endElementReached();

    void charactersReached();

    boolean unitIsReady();

    @NotNull
    XmppUnit readyUnit();

    void setErrorObserver(@NotNull final ErrorObserver observer);

//    /**
//     * Unique sequential code associated with concrete XMPPStreamParserStrategy instance
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
        void onError(@NotNull final XMPPStreamParser.Error error);
    }

}
