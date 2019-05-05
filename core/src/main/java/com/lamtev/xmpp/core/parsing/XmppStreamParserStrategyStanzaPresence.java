package com.lamtev.xmpp.core.parsing;

import com.lamtev.xmpp.core.XmppUnit;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamReader;

final class XmppStreamParserStrategyStanzaPresence extends XmppStreamParserStrategyStanza {
    XmppStreamParserStrategyStanzaPresence(@NotNull final XMLStreamReader reader) {
        super(reader);
    }

    @Override
    public void startElementReached(@NotNull final String name) {
        super.startElementReached(name);
    }

    @Override
    public void endElementReached() {
        super.endElementReached();
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
