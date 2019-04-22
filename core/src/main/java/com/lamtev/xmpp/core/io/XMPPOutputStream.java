package com.lamtev.xmpp.core.io;

import com.lamtev.xmpp.core.XMPPStreamFeatures;
import com.lamtev.xmpp.core.XMPPStreamHeader;
import com.lamtev.xmpp.core.XMPPUnit;
import com.lamtev.xmpp.core.serialization.XMPPUnitSerializer;
import gnu.trove.list.TByteList;
import gnu.trove.list.array.TByteArrayList;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.OutputStream;

public class XMPPOutputStream implements AutoCloseable {
    @NotNull
    private final OutputStream out;
    @NotNull
    private final XMPPUnitSerializer serializer;
    @NotNull
    private final TByteList buf = new TByteArrayList(1024);

    public XMPPOutputStream(@NotNull final OutputStream out, @NotNull final String encoding) {
        this.out = out;
        this.serializer = new XMPPUnitSerializer(encoding);
    }

    public void open(@NotNull final XMPPStreamHeader header, @NotNull final XMPPStreamFeatures features) {
        addUnitToBatch(header);
        addUnitToBatch(features);
        sendWholeBatch();
    }

    public void sendUnit(@NotNull final XMPPUnit unit) {
        try {
            out.write(serializer.serialize(unit));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addUnitToBatch(@NotNull final XMPPUnit unit) {
        buf.add(serializer.serialize(unit));
    }

    public void sendWholeBatch() {
        try {
            out.write(buf.toArray());
            buf.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {

    }
}
