package com.lamtev.xmpp.core.parsing;

import com.lamtev.xmpp.core.XmppStreamCloseTag;
import org.jetbrains.annotations.NotNull;

final class XmppStreamParserStrategyStreamCloseTag implements XmppStreamParserStrategy {
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
    public @NotNull XmppStreamCloseTag readyUnit() {
        return XmppStreamCloseTag.INSTANCE;
    }

    @Override
    public void setErrorObserver(@NotNull ErrorObserver observer) {

    }
}
