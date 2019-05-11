package com.lamtev.xmpp.core.parsing;

import com.lamtev.xmpp.core.XmppStreamCloseTag;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamReader;

final class XmppStreamParserStrategyStreamCloseTag extends XmppStreamParserAbstractStrategy {
    XmppStreamParserStrategyStreamCloseTag(@NotNull final XMLStreamReader reader) {
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
    public @NotNull XmppStreamCloseTag readyUnit() {
        return XmppStreamCloseTag.INSTANCE;
    }
}
