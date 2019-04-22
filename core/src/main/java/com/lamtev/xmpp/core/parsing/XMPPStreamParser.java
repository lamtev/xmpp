package com.lamtev.xmpp.core.parsing;


import com.lamtev.xmpp.core.XMPPStreamCloseTag;
import com.lamtev.xmpp.core.XMPPUnit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;

import static com.lamtev.xmpp.core.parsing.XMPPStreamParserStrategy.Name.*;
import static com.lamtev.xmpp.core.parsing.XMPPStreamParserStrategy.*;
import static javax.xml.stream.XMLStreamConstants.*;

public final class XMPPStreamParser {
    @NotNull
    private final XMLStreamReader reader;
    @Nullable
    private Delegate delegate;

    public XMPPStreamParser(@NotNull final InputStream inputStream, @NotNull final String encoding) throws XMPPStreamParserException {
        try {
            final var factory = XMLInputFactory.newInstance();
            reader = factory.createXMLStreamReader(inputStream, encoding);
        } catch (final XMLStreamException e) {
            final var message = "" + e.getMessage();
            throw new XMPPStreamParserException(message, e);
        }
    }

    public void startParsing() {
        boolean detectingMessageType = true;
        final var cache = new XMPPStreamParserStrategyCache(reader, (error) -> {
            if (delegate != null) {
                delegate.parserDidFailWithError(error);
            }
        });

        XMPPStreamParserStrategy strategy = null;
        try {
            int event = reader.getEventType();
            if (event == 7) {
                System.out.println("START_DOCUMENT");
            }
            while (reader.hasNext()) {
                event = reader.next();
                switch (event) {
                    case START_ELEMENT:
                        System.out.println("START_ELEMENT");
                        final var elementName = reader.getLocalName();
                        if (detectingMessageType) {
                            if (isPotentialStreamHeader(elementName)) {
                                strategy = cache.get(STREAM_HEADER);
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
                            detectingMessageType = false;
                        }
                        strategy.startElementReached();

                        if (strategy.unitIsReady() && delegate != null) {
                            System.out.println("didParseUnit");
                            delegate.parserDidParseUnit(strategy.readyUnit());

                        }
                        break;
                    case END_ELEMENT:
                        System.out.println("END_ELEMENT");
                        strategy.endElementReached();

                        if (strategy.unitIsReady() && delegate != null) {
                            System.out.println("didParseUnit");
                            delegate.parserDidParseUnit(strategy.readyUnit());
                        }
                        break;
                    case CHARACTERS:
                        System.out.println("CHARACTERS");
                        strategy.charactersReached();
                        break;
                    case END_DOCUMENT:
                        System.out.println("END_DOCUMENT");
                        if (delegate != null) {
                            delegate.parserDidParseUnit(XMPPStreamCloseTag.INSTANCE);
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

    public void setDelegate(@NotNull final Delegate delegate) {
        this.delegate = delegate;
    }

    public enum Error {
        INVALID_NAMESPACE,
        NOT_WELL_FORMED_XML,
        XML_INCORRECT_SYNTAX,
        UNRECOGNIZED_ELEMENT,
        //TODO
    }

    public interface Delegate {
        void parserDidParseUnit(@NotNull final XMPPUnit unit);

        void parserDidFailWithError(@NotNull final Error error);
    }
}
