package com.lamtev.xmpp.core.parsing;

import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamReader;
import java.util.function.Function;

final class XMPPStreamParserStrategyCache {
    @NotNull
    private final XMPPStreamParserStrategy[] cache = new XMPPStreamParserStrategy[XMPPStreamParserStrategy.Name.values().length];
    @SuppressWarnings("unchecked")
    @NotNull
    private final Function<XMLStreamReader, ? extends XMPPStreamParserStrategy>[] constructors = new Function[]{
            (Function<XMLStreamReader, XMPPStreamParserStrategyStreamHeader>) XMPPStreamParserStrategyStreamHeader::new,
            (Function<XMLStreamReader, XMPPStreamParserStrategyStreamFeatures>) XMPPStreamParserStrategyStreamFeatures::new,
            (Function<XMLStreamReader, XMPPStreamParserStrategySASLNegotiation>) XMPPStreamParserStrategySASLNegotiation::new,
            (Function<XMLStreamReader, XMPPStreamParserStrategyStanza>) XMPPStreamParserStrategyStanza::new,
            (Function<XMLStreamReader, XMPPStreamParserStrategyError>) XMPPStreamParserStrategyError::new,
    };
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
            cache[idx] = constructors[idx].apply(reader);
            cache[idx].setErrorObserver(errorObserver);
        }

        return cache[idx];
    }
}
