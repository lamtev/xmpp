package com.lamtev.xmpp.core.parsing;


import com.lamtev.xmpp.core.XmppUnit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;

import static com.lamtev.xmpp.core.parsing.XmppStreamParserStrategy.Name.*;
import static com.lamtev.xmpp.core.parsing.XmppStreamParserStrategy.*;
import static javax.xml.stream.XMLStreamConstants.*;

public final class XmppStreamParser {
    @NotNull
    private final InputStream in;
    @NotNull
    private final XMLInputFactory factory;
    @NotNull
    private XMLStreamReader reader;
    @Nullable
    private Delegate delegate;
    @NotNull
    private XmppStreamParserStrategyCache strategyCache;
    @Nullable
    private XmppStreamParserStrategy strategy;

    public XmppStreamParser(@NotNull final InputStream inputStream, @NotNull final String encoding) throws XmppStreamParserException {
        try {
            in = inputStream;
            factory = XMLInputFactory.newInstance();
            reader = factory.createXMLStreamReader(inputStream, encoding);
            strategyCache = new XmppStreamParserStrategyCache(reader, (error) -> {
                if (delegate != null) {
                    delegate.parserDidFailWithError(error);
                }
            });
        } catch (final XMLStreamException e) {
            final var message = "" + e.getMessage();
            throw new XmppStreamParserException(message, e);
        }
    }

    public void startParsing() {
        try {
            int event = reader.getEventType();
            while (reader.hasNext()) {
                event = reader.next();
                switch (event) {
                    case START_ELEMENT: {
                        final var elementName = reader.getLocalName();

                        boolean needNewStrategy = strategy == null;

                        if (needNewStrategy) {
                            if (isPotentialStreamHeader(elementName)) {
                                strategy = strategyCache.get(STREAM_HEADER);
                            } else if (isPotentialSaslNegotiation(elementName)) {
                                strategy = strategyCache.get(SASL_NEGOTIATION);
                            } else if (isPotentialStanzaIq(elementName)) {
                                strategy = strategyCache.get(STANZA_IQ);
                            } else if (isPotentialStanzaMessage(elementName)) {
                                strategy = strategyCache.get(STANZA_MESSAGE);
                            } else if (isPotentialStanzaPresence(elementName)) {
                                strategy = strategyCache.get(STANZA_PRESENCE);
                            } else if (isPotentialError(elementName)) {
                                strategy = strategyCache.get(ERROR);
                            } else {
                                if (delegate != null) {
                                    System.out.println(elementName);
                                    delegate.parserDidFailWithError(Error.UNRECOGNIZED_ELEMENT);
                                }
                                continue;
                            }
                            System.out.println(strategy + " set");
                        }

                        strategy.startElementReached(elementName);

                        if (strategy.unitIsReady() && delegate != null) {
                            delegate.parserDidParseUnit(strategy.readyUnit());
                            strategy = null;
                        }
                        break;
                    }
                    case END_ELEMENT: {
                        if (strategy == null) {
                            //TODO handle error
                        } else {
                            strategy.endElementReached();

                            if (strategy.unitIsReady() && delegate != null) {
                                delegate.parserDidParseUnit(strategy.readyUnit());
                                strategy = null;
                            }
                        }
                        break;
                    }
                    case CHARACTERS:
                        if (strategy == null) {
                            //TODO handle error
                        } else {
                            strategy.charactersReached();
                        }
                        break;
                    case END_DOCUMENT:
                        System.out.println("End document");
                        if (delegate != null) {
//                            delegate.parserDidParseUnit(XmppStreamCloseTag.INSTANCE);
                        }
                }
            }
        } catch (XMLStreamException e) {
            System.out.println(e.getMessage());
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

    public void reset() {
        try {
            reader.close();
            reader = factory.createXMLStreamReader(in);
            strategyCache.updateReader(reader);
            System.out.println("RESET");
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
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
        IQ_STANZA_TOP_ELEMENT_NOT_SUPPORTED,
        //TODO
    }

    public interface Delegate {
        void parserDidParseUnit(@NotNull final XmppUnit unit);

        void parserDidFailWithError(@NotNull final Error error);
    }
}
