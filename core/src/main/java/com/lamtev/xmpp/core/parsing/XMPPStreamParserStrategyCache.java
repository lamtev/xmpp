package com.lamtev.xmpp.core.parsing;

import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamReader;

final class XMPPStreamParserStrategyCache {
    @NotNull
    private final XMPPStreamParserStrategy[] cache = new XMPPStreamParserStrategy[XMPPStreamParserStrategy.Name.values().length];
    @NotNull
    private final XMLStreamReader reader;
    @NotNull
    private final XMPPStreamParserStrategy.ErrorObserver errorObserver;

    XMPPStreamParserStrategyCache(@NotNull final XMLStreamReader reader, @NotNull final XMPPStreamParserStrategy.ErrorObserver observer) {
        this.reader = reader;
        this.errorObserver = observer;
    }

    @NotNull
    XMPPStreamParserStrategy get(@NotNull final XMPPStreamParserStrategy.Name name) {
        final var idx = name.ordinal();
        if (cache[idx] == null) {
            switch (name) {
                case STREAM_HEADER:
                    cache[idx] = new XMPPStreamParserStrategyStreamHeader(reader);
                    break;
                case STANZA:
                    cache[idx] = new XMPPStreamParserStrategyStanza(reader);
                    break;
                case ERROR:
                    cache[idx] = new XMPPStreamParserStrategyError(reader);
                    break;
                default:
                    throw new IllegalStateException("Enum " + name.getDeclaringClass().getName() + " value \"" + name.toString() + "\" is not yet supported :(");
            }
            cache[idx].setErrorObserver(errorObserver);
        }

        return cache[idx];
    }
}
