package com.lamtev.xmpp.core.parsing;

import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamReader;

abstract class XmppStreamParserAbstractStrategy implements XmppStreamParserStrategy {
    @NotNull
    XMLStreamReader reader;
    @NotNull
    ErrorObserver errorObserver;

    XmppStreamParserAbstractStrategy(@NotNull final XMLStreamReader reader) {
        this.reader = reader;
    }

    @Override
    public void updateReader(@NotNull final XMLStreamReader reader) {
        this.reader = reader;
    }

    @Override
    public void setErrorObserver(@NotNull final ErrorObserver observer) {
        errorObserver = observer;
    }

    abstract void resetState();
}
