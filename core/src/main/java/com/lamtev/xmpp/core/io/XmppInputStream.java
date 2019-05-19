package com.lamtev.xmpp.core.io;

import com.lamtev.xmpp.core.*;
import com.lamtev.xmpp.core.parsing.XmppStreamParser;
import com.lamtev.xmpp.core.parsing.XmppStreamParserException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

//TODO: change exchange state
public final class XmppInputStream implements AutoCloseable, XmppStreamParser.Delegate {
    @NotNull
    private final InputStream in;
    @NotNull
    private final String encoding;
    @NotNull
    private XmppStreamParser parser;

    @NotNull
    private Handler handler;
    @SuppressWarnings("unchecked")
    @NotNull
    private final Consumer<? super XmppUnit>[] unitHandlers = new Consumer[]{
            (Consumer<XmppStreamHeader>) this::streamHeaderReceived,
            (Consumer<XmppStreamFeatures>) this::streamFeaturesReceived,
            (Consumer<XmppStreamCloseTag>) this::streamFeaturesCloseTagReceived,
            (Consumer<XmppStanza>) this::stanzaReceived,
            (Consumer<XmppError>) this::errorReceived,
            (Consumer<XmppSaslAuth>) this::saslAuthReceived,
            (Consumer<XmppSaslAuthSuccess>) this::saslAuthSuccessReceived,
    };
    @Nullable
    private XmppUnit unit;
    @Nullable
    private XmppStreamParser.Error error;
    @Nullable
    private XmppExchange exchange;

    public XmppInputStream(@NotNull final InputStream in, @NotNull final String encoding) throws XmppIOException {
        try {
            this.in = in;
            this.encoding = encoding;
            this.parser = new XmppStreamParser(in, encoding);
            this.parser.setDelegate(this);
        } catch (final XmppStreamParserException e) {
            final var message = "" + e.getMessage();
            throw new XmppIOException(message, e);
        }
    }

    public void setHandler(@NotNull final Handler handler) {
        this.handler = handler;
    }

    public void open() {
        parser.startParsing();
    }

    public void reopen() {
        parser.reset();
    }

    @Override
    public void close() throws IOException {
        parser.stopParsing();
        in.close();
        System.out.println("XmppInputStream closed");
    }

    public boolean hasError() {
        return error != null;
    }

    @NotNull
    public XmppStreamParser.Error error() {
        if (error == null) {
            throw new IllegalStateException("You should call hasError() before to ensure error is present");
        }

        return error;
    }

    @NotNull
    public XmppUnit unit() {
        if (unit == null) {
            final var msg = hasError() ? "You should call unit() if and only if hasError() returns false" : "Bug!";
            throw new IllegalStateException(msg);
        }

        return unit;
    }

    void setExchange(@NotNull final XmppExchange exchange) {
        this.exchange = exchange;
    }

    @Override
    public void parserDidParseUnit(@NotNull final XmppUnit unit) {
        System.out.println(unit + " parsed");

        unitHandlers[unit.code()].accept(unit);

        this.unit = unit;
        handler.handle();
    }

    @Override
    public void parserDidFailWithError(@NotNull final XmppStreamParser.Error error) {
        this.error = error;
        handler.handle();
    }

    private void streamHeaderReceived(@NotNull final XmppStreamHeader xmppStreamHeader) {

    }

    private void streamFeaturesReceived(@NotNull final XmppStreamFeatures xmppStreamFeatures) {

    }

    private void streamFeaturesCloseTagReceived(final XmppStreamCloseTag xmppStreamCloseTag) {

    }

    private void stanzaReceived(@NotNull final XmppStanza stanza) {

    }

    private void errorReceived(@NotNull final XmppError xmppError) {

    }

    private void saslAuthReceived(@NotNull final XmppSaslAuth xmppSaslAuth) {

    }

    private void saslAuthSuccessReceived(@NotNull final XmppSaslAuthSuccess xmppSaslAuthSuccess) {

    }

    public interface Handler {
        void handle();
    }
}
