package com.lamtev.xmpp.core.parsing;

import com.lamtev.xmpp.core.XmppStanza;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.stream.XMLStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static com.lamtev.xmpp.core.XmppStreamFeatures.Type.RESOURCE_BINDING;

final class XmppStreamParserStrategyStanzaIq extends XmppStreamParserStrategyStanza {
    @Nullable
    private Bind bind;
    @Nullable
    private Query query;

    XmppStreamParserStrategyStanzaIq(@NotNull final XMLStreamReader reader) {
        super(reader);
    }

    @Override
    public void startElementReached(@NotNull final String elName) {
        super.startElementReached(elName);

        if (openingTagCount == 2) {
            if ("bind".equals(elName) && RESOURCE_BINDING.toString().equals(reader.getNamespaceURI())) {
                bind = new Bind();
            } else if ("query".equals(elName) && XmppStanza.IqQuery.ContentNamespace.ROSTER.toString().equals(reader.getNamespaceURI())) {
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
        } else if (openingTagCount >= 3 && openingTagCount - closingTagCount == 3) {
            if (bind != null) {
                if ("resource".equals(elName)) {
                    bind.waitingForResource = true;
                } else if ("jid".equals(elName)) {
                    bind.waitingForJid = true;
                }
            } else if (query != null) {
                if ("item".equals(elName)) {
                    if (query.items == null) {
                        query.items = new ArrayList<>();
                    }
                    final var jid = reader.getAttributeValue(null, "jid");
                    final var askString = reader.getAttributeValue(null, "ask");
                    final var ask = XmppStanza.IqQuery.Item.Ask.of(askString);
                    final var name = reader.getAttributeValue(null, "name");
                    final var subscriptionString = reader.getAttributeValue(null, "subscription");
                    final var subscription = XmppStanza.IqQuery.Item.Subscription.of(subscriptionString);
                    if (jid != null) {
                        query.items.add(new XmppStanza.IqQuery.Item(ask, jid, name, subscription, null));
                    }
                }
            }
        } else if (openingTagCount >= 4 && openingTagCount - closingTagCount == 4) {
            if (query != null && query.items != null && !query.items.isEmpty() && "group".equals(elName)) {
                final var lastItem = query.items.get(query.items.size() - 1);
                if (lastItem.groups() == null) {
                    lastItem.setGroups(new LinkedHashSet<>());
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
                stanza = new XmppStanza(kind, to, from, id, type, lang, new XmppStanza.IqQuery(query.namespace, query.version, query.items));
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
            if (query.items != null && !query.items.isEmpty()) {
                final var lastItem = query.items.get(query.items.size() - 1);
                final var lastItemGroups = lastItem.groups();
                if (lastItemGroups != null) {
                    lastItemGroups.add(reader.getText());
                }
            }
        }
    }

    @Override
    void resetState() {
        super.resetState();

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
