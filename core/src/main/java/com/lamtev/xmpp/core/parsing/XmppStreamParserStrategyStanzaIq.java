package com.lamtev.xmpp.core.parsing;

import com.lamtev.xmpp.core.XmppStanza;
import com.lamtev.xmpp.core.XmppUnit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.stream.XMLStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.lamtev.xmpp.core.XmppStreamFeatures.Type.RESOURCE_BINDING;

final class XmppStreamParserStrategyStanzaIq extends XmppStreamParserStrategyStanza {
    @Nullable
    private Bind bind;
    @Nullable
    private Query query;

    @Nullable
    private XmppStanza stanza;

    XmppStreamParserStrategyStanzaIq(@NotNull final XMLStreamReader reader) {
        super(reader);
    }

    @Override
    public void startElementReached(@NotNull final String name) {
        super.startElementReached(name);

        if (openingTagCount == 2) {
            if ("bind".equals(name) && RESOURCE_BINDING.toString().equals(reader.getNamespaceURI())) {
                System.out.println("OK!");
                bind = new Bind();
            } else if ("query".equals(name) && XmppStanza.IqQuery.ContentNamespace.ROSTER.toString().equals(reader.getNamespaceURI())) {
                query = new Query(XmppStanza.IqQuery.ContentNamespace.ROSTER);

                for (int idx = 0; idx < reader.getAttributeCount(); idx++) {
                    if ("ver".equals(reader.getAttributeLocalName(idx))) {
                        query.version = reader.getAttributeValue(idx);
                    }
                }
            } else {
                if (kind == null) {
                    //TODO error
                    return;
                }
                if (id == null) {
                    //TODO error
                    return;
                }
                if (type == null) {
                    //TODO error
                    return;
                }
                stanza = new XmppStanza(kind, id, type, new XmppStanza.UnsupportedElement(reader.getLocalName()));
            }
        }

        if (openingTagCount == 3) {
            if (bind != null) {
                if ("resource".equals(name)) {
                    bind.waitingForResource = true;
                } else if ("jid".equals(name)) {
                    bind.waitingForJid = true;
                }
            } else if (query != null) {
                if ("item".equals(name)) {
                    if (query.items == null) {
                        query.items = new ArrayList<>();
                    }
                    final var jid = reader.getAttributeValue(null, "jid");
                    if (jid != null) {
                        query.items.add(new XmppStanza.IqQuery.Item(jid));
                    }
                }
            }
        }
    }

    @Override
    public void endElementReached() {
        super.endElementReached();

        if (kind == null) {
            //TODO error
            return;
        }

        if (tagCountsAreSame()) {
            if (bind != null) {
                if (reader.getLocalName().equals(kind.toString())) {
                    if (id == null) {
                        //TODO error
                        return;
                    }
                    if (type == null) {
                        //TODO error
                        return;
                    }
                    stanza = new XmppStanza(kind, id, type, new XmppStanza.IqBind(bind.resource, bind.jid));
                } else {
                    //TODO error
                }
                bind = null;
            } else if (query != null) {
                if (id == null) {
                    //TODO error
                    return;
                }
                if (type == null) {
                    //TODO error
                    return;
                }
                stanza = new XmppStanza(kind, id, type, from, to, lang, new XmppStanza.IqQuery(query.namespace, query.version, query.items));
                query = null;
            }
        }
    }

    @Override
    public void charactersReached() {
        if (bind != null) {
            if (bind.waitingForResource) {
                bind.waitingForResource = false;
                bind.resource = reader.getText();
            } else if (bind.waitingForJid) {
                bind.waitingForJid = false;
                bind.jid = reader.getText();
            }
        } else if (query != null) {

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
        resetState();

        return stanza;
    }

    @Override
    void resetState() {
        super.resetState();

        stanza = null;
        bind = null;
        query = null;
    }

    private static final class Bind {
        private boolean waitingForResource = false;
        private boolean waitingForJid = false;
        @Nullable
        private String resource;
        @Nullable
        private String jid;
    }

    private static final class Query {
        @NotNull
        private final XmppStanza.IqQuery.ContentNamespace namespace;
        @Nullable
        private String version;
        @Nullable
        private List<XmppStanza.IqQuery.Item> items;

        private Query(@NotNull final XmppStanza.IqQuery.ContentNamespace namespace) {
            this.namespace = namespace;
        }
    }
}
