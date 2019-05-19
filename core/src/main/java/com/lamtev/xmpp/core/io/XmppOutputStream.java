package com.lamtev.xmpp.core.io;

import com.lamtev.xmpp.core.*;
import com.lamtev.xmpp.core.serialization.XmppUnitSerializer;
import gnu.trove.list.TByteList;
import gnu.trove.list.array.TByteArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;

import static com.lamtev.xmpp.core.XmppUnit.CODE_STREAM_FEATURES;
import static com.lamtev.xmpp.core.io.XmppExchange.State.*;

public final class XmppOutputStream implements AutoCloseable {
    @NotNull
    private final OutputStream out;
    @NotNull
    private final String encoding;
    @NotNull
    private final XmppUnitSerializer serializer;
    @NotNull
    private final TByteList buf = new TByteArrayList(1024);
    @Nullable
    private XmppExchange exchange;
    //TODO: clearer name
    @NotNull
    private final Runnable[] featureHandlers = new Runnable[]{
            this::tlsFeaturesSent,
            this::saslFeaturesSent,
            this::bindingFeaturesSent,
    };
    //TODO: clearer name
    @Nullable
    private Runnable batchFeatureProcessor;

    public XmppOutputStream(@NotNull final OutputStream out, @NotNull final String encoding) {
        this.out = out;
        this.encoding = encoding;
        this.serializer = new XmppUnitSerializer(encoding);
    }

    public void open(@NotNull final XmppStreamHeader header, @NotNull final XmppStreamFeatures features) {
        addUnitToBatch(header);
        addUnitToBatch(features);
        sendWholeBatch();
    }

    //For development process
    public void sendUnit(@NotNull final String s) {
        try {
            out.write(s.getBytes(encoding));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendUnit(@NotNull final XmppUnit unit) {
        if (unit.code() == CODE_STREAM_FEATURES) {
            featureHandlers[((XmppStreamFeatures) unit).type().ordinal()].run();
        }

        try {
            out.write(serializer.serialize(unit));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (unit instanceof XmppStanza) {
            final var stanza = (XmppStanza) unit;
            if (stanza.kind() == XmppStanza.Kind.IQ && stanza.topElement() instanceof XmppStanza.IqBind) {
                if (exchange != null) {
                    exchange.changeState(EXCHANGE);
                }
            }
        } else if (unit instanceof XmppSaslAuthSuccess) {
            if (exchange != null) {
                exchange.changeState(WAITING_FOR_STREAM_HEADER);
            }
        }
    }

    public void addUnitToBatch(@NotNull final XmppUnit unit) {
        buf.add(serializer.serialize(unit));

        if (unit.code() == CODE_STREAM_FEATURES) {
            batchFeatureProcessor = featureHandlers[((XmppStreamFeatures) unit).type().ordinal()];
        }
    }

    public void sendWholeBatch() {
        //TODO feature processors
        try {
            out.write(buf.toArray());
            buf.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (batchFeatureProcessor != null) {
            batchFeatureProcessor.run();
            batchFeatureProcessor = null;
        }
    }

    @Override
    public void close() {
        System.out.println("XmppOutputStream closed");
    }

    void setExchange(@NotNull final XmppExchange exchange) {
        this.exchange = exchange;
    }

    private void tlsFeaturesSent() {
        if (exchange != null) {
            exchange.changeState(TLS_NEGOTIATION);
        }
    }

    private void saslFeaturesSent() {
        if (exchange != null) {
            exchange.changeState(SASL_NEGOTIATION);
        }
    }

    private void bindingFeaturesSent() {
        if (exchange != null) {
            exchange.changeState(RESOURCE_BINDING);
        }
    }
}
