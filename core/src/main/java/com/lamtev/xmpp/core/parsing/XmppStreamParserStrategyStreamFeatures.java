package com.lamtev.xmpp.core.parsing;

import com.lamtev.xmpp.core.XmppUnit;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamReader;

final class XmppStreamParserStrategyStreamFeatures implements XmppStreamParserStrategy {
    XmppStreamParserStrategyStreamFeatures(@NotNull final XMLStreamReader reader) {

    }

    @Override
    public void startElementReached(String name) {

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