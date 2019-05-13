package com.lamtev.xmpp.core.serialization;

import com.lamtev.xmpp.core.*;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.util.function.Consumer;

import static com.lamtev.xmpp.core.XmppStanza.TopElement.CODE_MESSAGE_BODY;
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
    private final Consumer<? super XmppStanza.TopElement>[] iqStanzaSerializers = new Consumer[]{
            (Consumer<XmppStanza.IqBind>) this::serializeIqStanzaBind,
            (Consumer<XmppStanza.IqQuery>) this::serializeIqStanzaQuery,
            (Consumer<XmppStanza.Error>) this::serializeStanzaError,
    };

    @SuppressWarnings("unchecked")
    private final Consumer<? super XmppStanza.TopElement>[] messageStanzaSerializers = new Consumer[]{
            (Consumer<XmppStanza.MessageBody>) this::serializeMessageStanzaBody,
            (Consumer<XmppStanza.Error>) this::serializeStanzaError,
    };
    @SuppressWarnings("unchecked")
    @NotNull
    private final Consumer<XmppStanza>[] stanzaSerializers = new Consumer[]{
            (Consumer<XmppStanza>) this::serializeMessageStanza,
            (Consumer<XmppStanza>) this::serializePresenceStanza,
            (Consumer<XmppStanza>) this::serializeIqStanza,
    };
    @SuppressWarnings("unchecked")
    @NotNull
    private final Consumer<? super XmppUnit>[] unitSerializers = new Consumer[]{
            (Consumer<XmppStreamHeader>) this::serializeStreamHeader,
            (Consumer<XmppStreamFeatures>) this::serializeStreamFeatures,
            (Consumer<XmppStanza>) this::serializeStanza,
            (Consumer<XmppError>) this::serializeError,
            (Consumer<XmppSaslAuth>) (any) -> {},
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
        unitSerializers[unit.code()].accept(unit);

        System.out.println(out.toString(UTF_8));

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
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    private void serializeStanza(@NotNull final XmppStanza stanza) {
        stanzaSerializers[stanza.kind().ordinal()].accept(stanza);
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
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    private void serializeMessageStanza(@NotNull final XmppStanza stanza) {
        try {
            writer.writeStartElement("message");
            serializeCommonAttributes(stanza);

            messageStanzaSerializers[stanza.topElement().code() - CODE_MESSAGE_BODY].accept(stanza.topElement());

            writer.writeEndElement();
            writer.flush();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    private void serializePresenceStanza(@NotNull final XmppStanza stanza) {
        //TODO
    }

    private void serializeIqStanza(@NotNull final XmppStanza stanza) {
        try {
            writer.writeStartElement("iq");
            serializeCommonAttributes(stanza);
            iqStanzaSerializers[stanza.topElement().code()].accept(stanza.topElement());

            writer.writeEndElement();
            writer.flush();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    private void serializeCommonAttributes(@NotNull final XmppStanza stanza) throws XMLStreamException {
        final var from = stanza.from();
        if (from != null) {
            writer.writeAttribute("from", from);
        }
        writer.writeAttribute("id", stanza.id());
        final var to = stanza.to();
        if (to != null) {
            writer.writeAttribute("to", to);
        }
        writer.writeAttribute("type", stanza.type().toString());
        final var lang = stanza.lang();
        if (lang != null) {
            writer.writeAttribute("xml:lang", lang);
        }
    }

    private void serializeIqStanzaBind(@NotNull final XmppStanza.IqBind bind) {
        try {
            writer.writeStartElement("bind");
            //TODO: XmppStreamFeatures.Type ???
            writer.writeDefaultNamespace(XmppStreamFeatures.Type.RESOURCE_BINDING.toString());
            if (bind.resource() != null) {
                writer.writeStartElement("resource");
                writer.writeCharacters(bind.resource());
                writer.writeEndElement();
            } else if (bind.jid() != null) {
                writer.writeStartElement("jid");
                writer.writeCharacters(bind.jid());
                writer.writeEndElement();
            }
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    private void serializeIqStanzaQuery(@NotNull final XmppStanza.IqQuery iqQuery) {
        try {
            writer.writeStartElement("query");
            writer.writeDefaultNamespace(iqQuery.namespace().toString());
            final var ver = iqQuery.version();
            if (ver != null) {
                writer.writeAttribute("ver", ver);
            }
            final var items = iqQuery.items();
            if (items != null) {
                for (final var it : items) {
                    writer.writeEmptyElement("item");
                    writer.writeAttribute("jid", it.jid());
                    if (it.name() != null) {
                        writer.writeAttribute("name", it.name());
                    }
                    if (it.subscription() != null) {
                        writer.writeAttribute("subscription", it.subscription().toString());
                    }
                }
            }
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    private void serializeStanzaError(@NotNull final XmppStanza.Error error) {
        try {
            writer.writeStartElement("error");
            writer.writeAttribute("type", error.type.toString());
            writer.writeStartElement(error.definedCondition.toString());
            writer.writeDefaultNamespace(error.definedCondition.namespace());
            writer.writeEndElement();
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    private void serializeMessageStanzaBody(@NotNull final XmppStanza.MessageBody messageBody) {
        try {
            writer.writeStartElement("body");
            writer.writeCharacters(messageBody.body());
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }
}
