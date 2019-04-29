package com.lamtev.xmpp.core.serialization;

import com.lamtev.xmpp.core.*;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.util.function.Consumer;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class XmppUnitSerializer {
    @NotNull
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    @NotNull
    private final String encoding;
    @NotNull
    private final XMLStreamWriter writer;
    @SuppressWarnings("unchecked")
    @NotNull
    private final Consumer<XmppStreamFeatures>[] streamFeatureSerializers = new Consumer[]{
            (tls) -> {},
            (Consumer<XmppStreamFeatures>) this::serializeStreamFeaturesSASL,
            (Consumer<XmppStreamFeatures>) this::serializeStreamFeaturesResourceBinding,
    };
    @SuppressWarnings("unchecked")
    @NotNull
    private final Consumer<? super XmppUnit>[] serializers = new Consumer[]{
            (Consumer<XmppStreamHeader>) this::serializeStreamHeader,
            (Consumer<XmppStreamFeatures>) this::serializeStreamFeatures,
            (Consumer<XmppStanza>) this::serializeStanza,
            (Consumer<XmppError>) this::serializeError,
            (Consumer<XmppSaslAuthSuccess>) this::serializeSaslAuthSuccess,
};

    public XmppUnitSerializer(@NotNull final String encoding) {
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
    public byte[] serialize(@NotNull final XmppUnit unit) {
        serializers[unit.code()].accept(unit);

        final var bytes = out.toByteArray();
        out.reset();

        return bytes;
    }

    private void serializeStreamHeader(@NotNull final XmppStreamHeader streamHeader) {
        try {
            writer.writeStartDocument(encoding, "1.0");
            writer.writeStartElement("stream", "stream", XmppStreamHeader.STREAM_NAMESPACE);
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
            writer.writeNamespace("stream", XmppStreamHeader.STREAM_NAMESPACE);
            writer.writeCharacters(null);
            writer.flush();
            System.out.println(out.toString(UTF_8));
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    private void serializeStreamFeatures(@NotNull final XmppStreamFeatures streamFeatures) {
        streamFeatureSerializers[streamFeatures.type().ordinal()].accept(streamFeatures);
    }

    private void serializeStreamFeaturesSASL(@NotNull final XmppStreamFeatures sasl) {
        try {
            writer.writeStartElement("stream", "features", XmppStreamHeader.STREAM_NAMESPACE);
            writer.writeStartElement("mechanisms");
            writer.writeDefaultNamespace(XmppStreamFeatures.Type.SASL.toString());
            for (final var mechanism : sasl.mechanisms()) {
                writer.writeStartElement("mechanism");
                writer.writeCharacters(mechanism.toString());
                writer.writeEndElement();
            }
            writer.writeEndElement();
            writer.writeEndElement();
            writer.flush();
            System.out.println(out.toString(UTF_8));
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    private void serializeStreamFeaturesResourceBinding(@NotNull final XmppStreamFeatures ignored) {
        try {
            writer.writeStartElement("stream", "features", XmppStreamHeader.STREAM_NAMESPACE);
            writer.writeEmptyElement("bind");
            writer.writeDefaultNamespace(XmppStreamFeatures.Type.RESOURCE_BINDING.toString());
            writer.writeEndElement();
            writer.flush();
            System.out.println(out.toString(UTF_8));
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    private void serializeStanza(@NotNull final XmppStanza stanza) {
        //TODO
    }

    private void serializeError(@NotNull final XmppError error) {
        //TODO
    }

    private void serializeSaslAuthSuccess(@NotNull final XmppSaslAuthSuccess saslAuthSuccess) {
        try {
            writer.writeStartElement("success");
            writer.writeDefaultNamespace(XmppSaslAuthSuccess.NAMESPACE);
            writer.writeEndElement();
            writer.flush();
//            out.writeBytes("/>".getBytes(UTF_8));
            System.out.println(out.toString(UTF_8));
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }
}
