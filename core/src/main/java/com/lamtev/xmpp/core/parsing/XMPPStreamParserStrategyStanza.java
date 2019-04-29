package com.lamtev.xmpp.core.parsing;

import com.lamtev.xmpp.core.XmppUnit;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamReader;

final class XMPPStreamParserStrategyStanza implements XMPPStreamParserStrategy {
    XMPPStreamParserStrategyStanza(@NotNull final XMLStreamReader reader) {

    }

    @Override
    public void startElementReached() {

    }

    @Override
    public void endElementReached() {

    }

    @Override
    public void charactersReached() {

    }

    @Override
    public boolean unitIsReady() {
        return false;
    }

    @Override
    public @NotNull XmppUnit readyUnit() {
        return null;
    }

    @Override
    public void setErrorObserver(@NotNull ErrorObserver observer) {

    }
}
