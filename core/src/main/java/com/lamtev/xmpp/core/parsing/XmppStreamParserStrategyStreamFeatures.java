package com.lamtev.xmpp.core.parsing;

import com.lamtev.xmpp.core.XmppUnit;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamReader;

final class XmppStreamParserStrategyStreamFeatures extends XmppStreamParserAbstractStrategy {
    XmppStreamParserStrategyStreamFeatures(@NotNull final XMLStreamReader reader) {
        super(reader);
    }

    @Override
    void resetState() {

    }

    @Override
    public void startElementReached(@NotNull final String name) {

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
}
