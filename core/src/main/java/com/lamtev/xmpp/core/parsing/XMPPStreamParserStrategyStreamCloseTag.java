package com.lamtev.xmpp.core.parsing;

import com.lamtev.xmpp.core.XMPPStreamCloseTag;
import org.jetbrains.annotations.NotNull;

public class XMPPStreamParserStrategyStreamCloseTag implements XMPPStreamParserStrategy {
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
    public @NotNull XMPPStreamCloseTag readyUnit() {
        return XMPPStreamCloseTag.INSTANCE;
    }

    @Override
    public void setErrorObserver(@NotNull ErrorObserver observer) {

    }
}
