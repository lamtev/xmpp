package com.lamtev.xmpp.core.serialization;

import com.lamtev.xmpp.core.*;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.util.function.Consumer;

import static com.lamtev.xmpp.core.XmppStanza.TopElement.CODE_MESSAGE_BODY;
import static com.lamtev.xmpp.core.XmppStanza.TopElement.CODE_PRESENCE_EMPTY;
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
    private final Consumer<? super XmppStanza.TopElement>[] iqStanzaSerializers = new Consumer[]{
            (Consumer<XmppStanza.IqBind>) this::serializeIqStanzaBind,
            (Consumer<XmppStanza.IqQuery>) this::serializeIqStanzaQuery,
            (Consumer<XmppStanza.Error>) this::serializeStanzaError,
            (Consumer<XmppStanza.UnsupportedElement>) this::serializeStanzaUnsupportedElement,
    };
    @SuppressWarnings("unchecked")
    @NotNull
    private final Consumer<? super XmppStanza.IqQuery.TopElement>[] iqStanzaQueryTopElementSerializers = new Consumer[]{
            (Consumer<XmppStanza.IqQuery.Item>) this::serializeIqStanzaQueryItem,
            (Consumer<XmppStanza.IqQuery.UnsupportedElement>) this::serializeIqStanzaQueryUnsupportedElement,
    };
    @SuppressWarnings("unchecked")
    @NotNull
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
            (Consumer<XmppStreamCloseTag>) this::serializeStreamCloseTag,
            (Consumer<XmppStanza>) this::serializeStanza,
            (Consumer<XmppError>) this::serializeError,
            (Consumer<XmppSaslAuth>) (any) -> {},
            (Consumer<XmppSaslAuthSuccess>) this::serializeSaslAuthSuccess,
            (Consumer<XmppSaslAuthFailure>) this::serializeSaslAuthFailure,
    };
    @SuppressWarnings("unchecked")
    @NotNull
    private final Consumer<? super XmppStanza.TopElement>[] presenceStanzaSerializers = new Consumer[]{
            (Consumer<XmppStanza.PresenceEmpty>) this::serializePresenceStanzaEmpty,
            (Consumer<XmppStanza.Error>) this::serializeStanzaError,
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

    private void serializeStreamCloseTag(final XmppStreamCloseTag streamCloseTag) {
        out.writeBytes("</stream>".getBytes(UTF_8));
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

    private void serializeSaslAuthFailure(@NotNull final XmppSaslAuthFailure saslAuthFailure) {
        try {
            writer.writeStartElement("failure");
            writer.writeDefaultNamespace(XmppSaslAuthFailure.NAMESPACE);
            writer.writeEmptyElement("not-authorized");
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
        try {
            writer.writeStartElement("presence");
            serializeCommonAttributes(stanza);
            presenceStanzaSerializers[stanza.topElement().code() - CODE_PRESENCE_EMPTY].accept(stanza.topElement());
            writer.writeEndElement();
            writer.flush();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    private void serializePresenceStanzaEmpty(final XmppStanza.PresenceEmpty presenceEmpty) {}

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
        final var id = stanza.id();
        if (id != null) {
            writer.writeAttribute("id", stanza.id());
        }
        final var to = stanza.to();
        if (to != null) {
            writer.writeAttribute("to", to);
        }
        final var type = stanza.type();
        if (type != null) {
            writer.writeAttribute("type", type.toString());
        }
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
            final var items = iqQuery.topElements();
            if (items != null) {
                items.forEach(item -> iqStanzaQueryTopElementSerializers[item.code()].accept(item));
            }
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    private void serializeIqStanzaQueryItem(@NotNull final XmppStanza.IqQuery.Item item) {
        final var groups = item.groups();
        try {
            if (groups != null) {
                writer.writeStartElement("item");
            } else {
                writer.writeEmptyElement("item");
            }
            writer.writeAttribute("jid", item.jid());
            final var name = item.name();
            if (name != null) {
                writer.writeAttribute("name", name);
            }
            final var subscription = item.subscription();
            if (subscription != null) {
                writer.writeAttribute("subscription", subscription.toString());

            }
            if (groups != null) {
                for (final var group : groups) {
                    writer.writeStartElement("group");
                    writer.writeCharacters(group);
                    writer.writeEndElement();
                }
                writer.writeEndElement();
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    private void serializeIqStanzaQueryUnsupportedElement(@NotNull final XmppStanza.IqQuery.UnsupportedElement unsupportedElement) {
        try {
            writer.writeEmptyElement(unsupportedElement.name);
            final var namespace = unsupportedElement.namespace();
            if (namespace != null) {
                writer.writeDefaultNamespace(namespace.toString());
            }
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

    private void serializeStanzaUnsupportedElement(@NotNull final XmppStanza.UnsupportedElement unsupportedElement) {
        try {
            writer.writeEmptyElement(unsupportedElement.name);
            writer.writeDefaultNamespace(unsupportedElement.namespace);
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
