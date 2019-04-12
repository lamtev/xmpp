package com.lamtev.xmpp.core.io;

import com.lamtev.xmpp.core.XMPPUnit;
import com.lamtev.xmpp.core.parsing.XMPPStreamParser;
import com.lamtev.xmpp.core.parsing.XMPPStreamParserException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;

public class XMPPInputStream implements AutoCloseable, XMPPStreamParser.Delegate {
    @NotNull
    private final InputStream in;
    @NotNull
    private final XMPPStreamParser parser;

    @Nullable
    private Handler handler;
    @Nullable
    private XMPPUnit unit;
    @Nullable
    private XMPPStreamParser.Error error;

    public XMPPInputStream(@NotNull final InputStream in, @NotNull final String encoding) throws XMPPIOException {
        try {
            this.in = in;
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
    public XMPPUnit unit() {
        if (unit == null) {
            final var msg = hasError() ? "You should call unit() if and only if hasError() returns false" : "Bug!";
            throw new IllegalStateException(msg);
        }

        return unit;
    }

    @Override
    public void parserDidParseUnit(@NotNull final XMPPUnit unit) {
        if (handler != null) {
            this.unit = unit;
            handler.handle();
        }
    }

    @Override
    public void parserDidFailWithError(@NotNull final XMPPStreamParser.Error error) {
        if (handler != null) {
            this.error = error;
            handler.handle();
        }
    }

    public interface Handler {
        void handle();
    }
}
