package com.lamtev.xmpp.core.serialization;

import com.lamtev.xmpp.core.*;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.util.function.Consumer;

public final class XMPPUnitSerializer {
    @NotNull
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    @NotNull
    private final String encoding;
    @NotNull
    private final XMLStreamWriter writer;
    @SuppressWarnings("unchecked")
    @NotNull
    private final Consumer<XMPPStreamFeatures>[] streamFeatureSerializers = new Consumer[]{
            (tls) -> {
            },
            (Consumer<XMPPStreamFeatures>) this::serializeStreamFeaturesSASL,
            (Consumer<XMPPStreamFeatures>) this::serializeStreamFeaturesResourceBinding,
    };
    @SuppressWarnings("unchecked")
    @NotNull
    private final Consumer<? super XMPPUnit>[] serializers = new Consumer[]{
            (Consumer<XMPPStreamHeader>) this::serializeStreamHeader,
            (Consumer<XMPPStreamFeatures>) this::serializeStreamFeatures,
            (Consumer<XMPPStanza>) this::serializeStanza,
            (Consumer<XMPPError>) this::serializeError,
    };

    public XMPPUnitSerializer(@NotNull final String encoding) {
        this.encoding = encoding;
        final var factory = XMLOutputFactory.newFactory();
        //TODO
        try {
            this.writer = factory.createXMLStreamWriter(out, encoding);
        } catch (XMLStreamException e) {
            throw new IllegalStateException(e);
        }
    }

    @NotNull
    public byte[] serialize(@NotNull final XMPPUnit unit) {
        serializers[unit.code()].accept(unit);

        final var bytes = out.toByteArray();
        out.reset();

        return bytes;
    }

    private void serializeStreamHeader(@NotNull final XMPPStreamHeader streamHeader) {
        try {
            writer.writeStartDocument(encoding, "1.0");
            writer.writeStartElement("stream", "stream", XMPPStreamHeader.STREAM_NAMESPACE);
            final var from = streamHeader.from();
            if (from != null) {
                writer.writeAttribute("from", from);
            }
            final var id = streamHeader.id();
            if (id != null) {
                writer.writeAttribute("id", id);
            }
            final var to = streamHeader.to();
            if (to != null) {
                writer.writeAttribute("to", to);
            }
            writer.writeAttribute("version", Float.toString(streamHeader.version()));
            writer.writeAttribute("xml:lang", "en");
            writer.writeDefaultNamespace(streamHeader.contentNamespace().toString());
            writer.writeNamespace("stream", XMPPStreamHeader.STREAM_NAMESPACE);
            writer.writeCharacters(null);
            writer.flush();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    private void serializeStreamFeatures(@NotNull final XMPPStreamFeatures streamFeatures) {
        streamFeatureSerializers[streamFeatures.type().ordinal()].accept(streamFeatures);
    }

    private void serializeStreamFeaturesSASL(@NotNull final XMPPStreamFeatures sasl) {
        try {
            writer.writeStartElement("stream", "features", XMPPStreamHeader.STREAM_NAMESPACE);
            writer.writeStartElement("mechanisms");
            writer.writeDefaultNamespace(XMPPStreamFeatures.Type.SASL.toString());
            for (final var mechanism : sasl.mechanisms()) {
                writer.writeStartElement("mechanism");
                writer.writeCharacters(mechanism.toString());
                writer.writeEndElement();
            }
            writer.writeEndElement();
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    private void serializeStreamFeaturesResourceBinding(@NotNull final XMPPStreamFeatures ignored) {
        try {
            writer.writeStartElement("stream", "features", XMPPStreamHeader.STREAM_NAMESPACE);
            writer.writeEmptyElement("bind");
            writer.writeDefaultNamespace(XMPPStreamFeatures.Type.RESOURCE_BINDING.toString());
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    private void serializeStanza(@NotNull final XMPPStanza stanza) {
        //TODO
    }

    private void serializeError(@NotNull final XMPPError error) {
        //TODO
    }
}
