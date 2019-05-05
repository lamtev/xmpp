package com.lamtev.xmpp.core.parsing;

import com.lamtev.xmpp.core.XmppStanza;
import com.lamtev.xmpp.core.XmppStreamFeatures;
import com.lamtev.xmpp.core.XmppUnit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.stream.XMLStreamReader;

final class XmppStreamParserStrategyStanzaIq extends XmppStreamParserStrategyStanza {
    @Nullable
    private String resource;
    @Nullable
    private String jid;
    private boolean waitingForResource = false;
    private boolean waitingForJid = false;

    @Nullable
    private XmppStanza stanza;

    XmppStreamParserStrategyStanzaIq(@NotNull final XMLStreamReader reader) {
        super(reader);
    }

    @Override
    public void startElementReached(@NotNull final String name) {
        super.startElementReached(name);

        if (tagCount > 0) {
            if ("bind".equals(name) && XmppStreamFeatures.Type.RESOURCE_BINDING.toString().equals(reader.getNamespaceURI())) {
                System.out.println("OK!");
            } else if ("resource".equals(name)) {
                waitingForResource = true;
            } else if ("jid".equals(name)) {
                waitingForJid = true;
            }
        }
    }

    @Override
    public void endElementReached() {
        super.endElementReached();

        if (tagCount == 0) {
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
}
