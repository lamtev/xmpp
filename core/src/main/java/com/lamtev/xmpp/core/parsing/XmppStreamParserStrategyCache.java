package com.lamtev.xmpp.core.parsing;

import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamReader;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

final class XmppStreamParserStrategyCache {
    @NotNull
    private final XmppStreamParserStrategy[] cache = new XmppStreamParserStrategy[XmppStreamParserStrategy.Name.values().length];
    @SuppressWarnings("unchecked")
    @NotNull
    private final Function<XMLStreamReader, ? extends XmppStreamParserStrategy>[] constructors = new Function[]{
            (Function<XMLStreamReader, XmppStreamParserStrategyStreamHeader>) XmppStreamParserStrategyStreamHeader::new,
            (Function<XMLStreamReader, XmppStreamParserStrategyStreamFeatures>) XmppStreamParserStrategyStreamFeatures::new,
            (Function<XMLStreamReader, XmppStreamParserStrategySaslNegotiation>) XmppStreamParserStrategySaslNegotiation::new,
            (Function<XMLStreamReader, XmppStreamParserStrategyStanzaIq>) XmppStreamParserStrategyStanzaIq::new,
            (Function<XMLStreamReader, XmppStreamParserStrategyStanzaMessage>) XmppStreamParserStrategyStanzaMessage::new,
            (Function<XMLStreamReader, XmppStreamParserStrategyStanzaPresence>) XmppStreamParserStrategyStanzaPresence::new,
            (Function<XMLStreamReader, XmppStreamParserStrategyError>) XmppStreamParserStrategyError::new,
    };
    @NotNull
    private XMLStreamReader reader;
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

    void updateReader(@NotNull final XMLStreamReader reader) {
        this.reader = reader;
        Arrays.stream(cache).filter(Objects::nonNull).forEach(it -> it.updateReader(reader));
    }
}
