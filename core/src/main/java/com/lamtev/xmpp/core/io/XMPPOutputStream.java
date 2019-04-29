package com.lamtev.xmpp.core.io;

import com.lamtev.xmpp.core.XmppSaslAuthSuccess;
import com.lamtev.xmpp.core.XmppStreamFeatures;
import com.lamtev.xmpp.core.XmppStreamHeader;
import com.lamtev.xmpp.core.XmppUnit;
import com.lamtev.xmpp.core.serialization.XMPPUnitSerializer;
import gnu.trove.list.TByteList;
import gnu.trove.list.array.TByteArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;

public final class XMPPOutputStream implements AutoCloseable {
    @NotNull
    private final OutputStream out;
    @NotNull
    private final XMPPUnitSerializer serializer;
    @NotNull
    private final TByteList buf = new TByteArrayList(1024);
    @Nullable
    private XMPPExchange exchange;
    //TODO: clearer name
    @NotNull
    private final Runnable[] featureProcessors = new Runnable[]{
            this::tlsFeaturesSent,
            this::saslFeaturesSent,
            this::bindingFeaturesSent,
    };
    //TODO: clearer name
    @Nullable
    private Runnable batchFeatureProcessor;

    public XMPPOutputStream(@NotNull final OutputStream out, @NotNull final String encoding) {
        this.out = out;
        this.serializer = new XMPPUnitSerializer(encoding);
    }

    public void open(@NotNull final XmppStreamHeader header, @NotNull final XmppStreamFeatures features) {
        addUnitToBatch(header);
        addUnitToBatch(features);
        sendWholeBatch();
    }

    public void sendUnit(@NotNull final XmppUnit unit) {
        if (unit.code() == XmppUnit.CODE_STREAM_FEATURES) {
            featureProcessors[((XmppStreamFeatures) unit).type().ordinal()].run();
        }

        try {
            out.write(serializer.serialize(unit));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (unit instanceof XmppSaslAuthSuccess) {
            if (exchange != null) {
                exchange.setState(XMPPExchange.State.RESOURCE_BINDING);
            }
        }
    }

    public void addUnitToBatch(@NotNull final XmppUnit unit) {
        buf.add(serializer.serialize(unit));

        if (unit.code() == XmppUnit.CODE_STREAM_FEATURES) {
            batchFeatureProcessor = featureProcessors[((XmppStreamFeatures) unit).type().ordinal()];
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

    }

    void setExchange(@NotNull final XMPPExchange exchange) {
        this.exchange = exchange;
    }

    private void tlsFeaturesSent() {
        if (exchange != null) {
            exchange.setState(XMPPExchange.State.TLS_NEGOTIATION);
        }
    }

    private void saslFeaturesSent() {
        if (exchange != null) {
            exchange.setState(XMPPExchange.State.SASL_NEGOTIATION);
        }
    }

    private void bindingFeaturesSent() {
        if (exchange != null) {
            exchange.setState(XMPPExchange.State.RESOURCE_BINDING);
        }
    }
}
