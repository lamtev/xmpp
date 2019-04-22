package com.lamtev.xmpp.core.parsing;

import com.lamtev.xmpp.core.XMPPUnit;
import org.jetbrains.annotations.NotNull;

interface XMPPStreamParserStrategy {

    static boolean isPotentialStreamHeader(@NotNull final String element) {
        return "stream".equals(element);
    }

    static boolean isPotentialStreamFeatures(@NotNull final String element) {
        return "features".equals(element);
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
    XMPPUnit readyUnit();

    void setErrorObserver(@NotNull final ErrorObserver observer);

//    /**
//     * Unique sequential code associated with concrete XMPPStreamParserStrategy instance
//     * @see XMPPUnit#code()
//     * @return
//     */
//    int code();

    enum Name {
        STREAM_HEADER,
        STANZA,
        ERROR,
    }

    interface ErrorObserver {
        void onError(@NotNull final XMPPStreamParser.Error error);
    }

}
