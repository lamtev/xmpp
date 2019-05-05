package com.lamtev.xmpp.core.parsing;

import com.lamtev.xmpp.core.XmppStanza;
import com.lamtev.xmpp.core.XmppStreamFeatures;
import com.lamtev.xmpp.core.XmppUnit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.stream.XMLStreamReader;

final class XmppStreamParserStrategyStanzaIq extends XmppStreamParserAbstractStrategy {
    @Nullable
    private XmppStanza.Kind kind;
    @Nullable
    private String id;
    @Nullable
    private XmppStanza.TypeAttribute type;
    @Nullable
    private String resource;
    @Nullable
    private String jid;
    private boolean waitingForResource = false;
    private boolean waitingForJid = false;

    private int openingTagCount = 0;
    private int closingTagCount = 0;

    @Nullable
    private XmppStanza stanza;

    XmppStreamParserStrategyStanzaIq(@NotNull final XMLStreamReader reader) {
        super(reader);
    }

    @Override
    public void startElementReached(@NotNull final String name) {
        ++openingTagCount;

        System.out.println(name);
        if (kind == null) {
            System.out.println("Stanza!!!");
            kind = XmppStanza.Kind.of(name);

            for (int i = 0; i < reader.getAttributeCount(); ++i) {
                switch (reader.getAttributeLocalName(i)) {
                    case "id":
                        id = reader.getAttributeValue(i);
                        break;
                    case "type":
                        type = XmppStanza.TypeAttribute.of(kind, reader.getAttributeValue(i));
                        break;
                }
            }
        } else {
            switch (kind) {
                case IQ:
                    if ("bind".equals(name) && XmppStreamFeatures.Type.RESOURCE_BINDING.toString().equals(reader.getNamespaceURI())) {
                        System.out.println("OK!");
                    } else if ("resource".equals(name)) {
                        waitingForResource = true;
                    } else if ("jid".equals(name)) {
                        waitingForJid = true;
                    }
            }
        }
    }

    @Override
    public void endElementReached() {
        if (++closingTagCount == openingTagCount) {
            if (kind == null) {
                //TODO error
            } else if (reader.getLocalName().equals(kind.toString())) {
                if (id == null) {
                    //TODO error
                    return;
                }
                if (type == null) {
                    //TODO error
                    return;
                }
                stanza = new XmppStanza(kind, id, type, new XmppStanza.IqBind(resource, jid));
            } else {
                //TODO error
            }
        }
    }

    @Override
    public void charactersReached() {
        if (waitingForResource) {
            waitingForResource = false;
            resource = reader.getText();
        } else if (waitingForJid) {
            waitingForJid = false;
            jid = reader.getText();
        }
    }

    @Override
    public boolean unitIsReady() {
        return stanza != null;
    }

    @Override
    public @NotNull XmppUnit readyUnit() {
        if (stanza == null) {
            throw new IllegalStateException("");
        }
        final var stanza = this.stanza;
        this.stanza = null;

        return stanza;
    }

    @Override
    public void setErrorObserver(@NotNull ErrorObserver observer) {

    }
}
