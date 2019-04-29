package com.lamtev.xmpp.core.parsing;


import com.lamtev.xmpp.core.XmppStreamCloseTag;
import com.lamtev.xmpp.core.XmppUnit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;

import static com.lamtev.xmpp.core.parsing.XMPPStreamParserStrategy.Name.*;
import static com.lamtev.xmpp.core.parsing.XMPPStreamParserStrategy.*;
import static javax.xml.stream.XMLStreamConstants.*;

public final class XMPPStreamParser {
    @NotNull
    private final InputStream in;
    @NotNull
    private final XMLInputFactory factory;
    @NotNull
    private XMLStreamReader reader;
    @Nullable
    private Delegate delegate;
    @NotNull
    private Deque<XMPPStreamParserStrategy> strategyStack = new ArrayDeque<>(5);

    public XMPPStreamParser(@NotNull final InputStream inputStream, @NotNull final String encoding) throws XMPPStreamParserException {
        try {
            in = inputStream;
            factory = XMLInputFactory.newInstance();
            reader = factory.createXMLStreamReader(inputStream, encoding);
        } catch (final XMLStreamException e) {
            final var message = "" + e.getMessage();
            throw new XMPPStreamParserException(message, e);
        }
    }

    public void startParsing() {
        final var cache = new XMPPStreamParserStrategyCache(reader, (error) -> {
            if (delegate != null) {
                delegate.parserDidFailWithError(error);
            }
        });

        try {
            int event = reader.getEventType();
            while (reader.hasNext()) {
                event = reader.next();
                switch (event) {
                    case START_ELEMENT: {
                        final var elementName = reader.getLocalName();
                        XMPPStreamParserStrategy strategy = null;
                        if (isPotentialStreamHeader(elementName)) {
                            strategy = cache.get(STREAM_HEADER);
                        } else if (isPotentialSASLNegotiation(elementName)) {
                            strategy = cache.get(SASL_NEGOTIATION);
                        } else if (isPotentialStanza(elementName)) {
                            strategy = cache.get(STANZA);
                        } else if (isPotentialError(elementName)) {
                            strategy = cache.get(ERROR);
                        } else {
                            if (delegate != null) {
                                delegate.parserDidFailWithError(Error.UNRECOGNIZED_ELEMENT);
                                return;
                            }
                        }
                        strategyStack.push(strategy);
                        strategy.startElementReached(elementName);

                        if (strategy.unitIsReady() && delegate != null) {
                            delegate.parserDidParseUnit(strategy.readyUnit());
                        }
                        break;
                    }
                    case END_ELEMENT: {
                        final var strategy = strategyStack.pop();
                        strategy.endElementReached();

                        if (strategy.unitIsReady() && delegate != null) {
                            delegate.parserDidParseUnit(strategy.readyUnit());
                        }
                        break;
                    }
                    case CHARACTERS:
                        final var strategy = strategyStack.peekFirst();
                        strategy.charactersReached();
                        break;
                    case END_DOCUMENT:
                        if (delegate != null) {
                            delegate.parserDidParseUnit(XmppStreamCloseTag.INSTANCE);
                        }
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
            if (delegate != null) {
                delegate.parserDidFailWithError(Error.NOT_WELL_FORMED_XML);
            }
        }
    }

    public void stopParsing() {
        try {
            reader.close();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    public void restart() {
        try {
//            reader.close();
            reader = factory.createXMLStreamReader(in);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }

        startParsing();
    }

    public void setDelegate(@NotNull final Delegate delegate) {
        this.delegate = delegate;
    }

    public enum Error {
        INVALID_NAMESPACE,
        NOT_WELL_FORMED_XML,
        XML_INCORRECT_SYNTAX,
        UNRECOGNIZED_ELEMENT,
        SASL_INVALID_MECHANISM,
        SASL_MALFORMED_REQUEST,
        //TODO
    }

    public interface Delegate {
        void parserDidParseUnit(@NotNull final XmppUnit unit);

        void parserDidFailWithError(@NotNull final Error error);
    }
}
