package com.lamtev.xmpp.core.parsing;

import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamReader;
import java.util.function.Function;

final class XmppStreamParserStrategyCache {
    @NotNull
    private final XmppStreamParserStrategy[] cache = new XmppStreamParserStrategy[XmppStreamParserStrategy.Name.values().length];
    @SuppressWarnings("unchecked")
    @NotNull
    private final Function<XMLStreamReader, ? extends XmppStreamParserStrategy>[] constructors = new Function[]{
            (Function<XMLStreamReader, XmppStreamParserStrategyStreamHeader>) XmppStreamParserStrategyStreamHeader::new,
            (Function<XMLStreamReader, XmppStreamParserStrategyStreamFeatures>) XmppStreamParserStrategyStreamFeatures::new,
            (Function<XMLStreamReader, XmppStreamParserStrategySASLNegotiation>) XmppStreamParserStrategySASLNegotiation::new,
            (Function<XMLStreamReader, XmppStreamParserStrategyStanza>) XmppStreamParserStrategyStanza::new,
            (Function<XMLStreamReader, XmppStreamParserStrategyError>) XmppStreamParserStrategyError::new,
    };
    @NotNull
    private final XMLStreamReader reader;
    @NotNull
    private final XmppStreamParserStrategy.ErrorObserver errorObserver;

    XmppStreamParserStrategyCache(@NotNull final XMLStreamReader reader, @NotNull final XmppStreamParserStrategy.ErrorObserver observer) {
        this.reader = reader;
        this.errorObserver = observer;
    }

    @NotNull
    XmppStreamParserStrategy get(@NotNull final XmppStreamParserStrategy.Name name) {
        final var idx = name.ordinal();
        if (cache[idx] == null) {
            cache[idx] = constructors[idx].apply(reader);
            cache[idx].setErrorObserver(errorObserver);
        }

        return cache[idx];
    }
}
