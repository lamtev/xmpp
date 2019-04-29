package com.lamtev.xmpp.core.io;

import com.lamtev.xmpp.core.XmppStreamFeatures;
import com.lamtev.xmpp.core.XmppUnit;
import com.lamtev.xmpp.core.parsing.XMPPStreamParser;
import com.lamtev.xmpp.core.parsing.XMPPStreamParserException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;

//TODO: change exchange state
public final class XMPPInputStream implements AutoCloseable, XMPPStreamParser.Delegate {
    @NotNull
    private final InputStream in;
    @NotNull
    private final String encoding;
    @NotNull
    private XMPPStreamParser parser;

    @NotNull
    private Handler handler;
    @NotNull
    private final Runnable[] featureProcessors = new Runnable[]{
            this::tlsFeaturesReceived,
            this::saslFeaturesReceived,
            this::bindingFeaturesReceived,
    };
    @Nullable
    private XmppUnit unit;
    @Nullable
    private XMPPStreamParser.Error error;
    @Nullable
    private XMPPExchange exchange;

    public XMPPInputStream(@NotNull final InputStream in, @NotNull final String encoding) throws XMPPIOException {
        try {
            this.in = in;
            this.encoding = encoding;
            this.parser = new XMPPStreamParser(in, encoding);
            this.parser.setDelegate(this);
        } catch (final XMPPStreamParserException e) {
            final var message = "" + e.getMessage();
            throw new XMPPIOException(message, e);
        }
    }

    public void setHandler(@NotNull final Handler handler) {
        this.handler = handler;
    }

    public void open() {
        parser.startParsing();
    }

    public void reopen() {
        parser.restart();
    }

    @Override
    public void close() throws IOException {
        parser.stopParsing();
        in.close();
    }

    public boolean hasError() {
        return error != null;
    }

    @NotNull
    public XMPPStreamParser.Error error() {
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

    void setExchange(@NotNull final XMPPExchange exchange) {
        this.exchange = exchange;
    }

    @Override
    public void parserDidParseUnit(@NotNull final XmppUnit unit) {
        if (unit instanceof XmppStreamFeatures) {
            featureProcessors[((XmppStreamFeatures) unit).type().ordinal()].run();
        }

        this.unit = unit;
        handler.handle();
    }

    @Override
    public void parserDidFailWithError(@NotNull final XMPPStreamParser.Error error) {
        this.error = error;
        handler.handle();
    }

    private void tlsFeaturesReceived() {
        if (exchange != null) {
            exchange.setState(XMPPExchange.State.TLS_NEGOTIATION);
        }
    }

    private void saslFeaturesReceived() {
        if (exchange != null) {
            exchange.setState(XMPPExchange.State.SASL_NEGOTIATION);
        }
    }

    private void bindingFeaturesReceived() {
        if (exchange != null) {
            exchange.setState(XMPPExchange.State.RESOURCE_BINDING);
        }
    }

    public interface Handler {
        void handle();
    }
}
