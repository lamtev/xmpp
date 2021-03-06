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
    @Nullable
    private XmppStanza.UnsupportedElement unsupportedElement;

    XmppStreamParserStrategyStanzaIq(@NotNull final XMLStreamReader reader) {
        super(reader);
    }

    @Override
    public void startElementReached(@NotNull final String elName) {
        super.startElementReached(elName);

        if (openingTagCount == 2) {
            if ("bind".equals(elName) && RESOURCE_BINDING.toString().equals(reader.getNamespaceURI())) {
                bind = new Bind();
            } else if ("query".equals(elName) && XmppStanza.IqQuery.SupportedContentNamespace.ROSTER.toString().equals(reader.getNamespaceURI())) {
                query = new Query(XmppStanza.IqQuery.SupportedContentNamespace.ROSTER);

                for (int idx = 0; idx < reader.getAttributeCount(); idx++) {
                    if ("ver".equals(reader.getAttributeLocalName(idx))) {
                        query.version = reader.getAttributeValue(idx);
                    }
                }
            } else if ("query".equals(elName)) {
                query = new Query(new XmppStanza.IqQuery.UnsupportedContentNamespace(reader.getNamespaceURI()));
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
                unsupportedElement = new XmppStanza.UnsupportedElement(reader.getLocalName(), reader.getNamespaceURI(), XmppStanza.TopElement.CODE_IQ_UNSUPPORTED);
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
                    if (query.topElements == null) {
                        query.topElements = new ArrayList<>();
                    }
                    final var jid = reader.getAttributeValue(null, "jid");
                    final var askString = reader.getAttributeValue(null, "ask");
                    final var ask = XmppStanza.IqQuery.Item.Ask.of(askString);
                    final var name = reader.getAttributeValue(null, "name");
                    final var subscriptionString = reader.getAttributeValue(null, "subscription");
                    final var subscription = XmppStanza.IqQuery.Item.Subscription.of(subscriptionString);
                    if (jid != null) {
                        query.topElements.add(new XmppStanza.IqQuery.Item(ask, jid, name, subscription, null));
                    }
                } else {
                    if (query.topElements == null) {
                        query.topElements = new ArrayList<>();
                    }

                    query.topElements.add(new XmppStanza.IqQuery.UnsupportedElement(elName, reader.getNamespaceURI()));
                }
            }
        } else if (openingTagCount >= 4 && openingTagCount - closingTagCount == 4) {
            if (query != null && query.topElements != null && !query.topElements.isEmpty() && "group".equals(elName)) {
                final var lastItem = (XmppStanza.IqQuery.Item) query.topElements.get(query.topElements.size() - 1);
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
                stanza = new XmppStanza(kind, to, from, id, type, lang, new XmppStanza.IqQuery(query.namespace, query.version, query.topElements));
                query = null;
            } else if (unsupportedElement != null) {
                stanza = new XmppStanza(kind, id, type, unsupportedElement);
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
            if (query.topElements != null && !query.topElements.isEmpty()) {
                final var lastTopElement = query.topElements.get(query.topElements.size() - 1);
                if (lastTopElement instanceof XmppStanza.IqQuery.Item) {
                    final var lastItem = (XmppStanza.IqQuery.Item) lastTopElement;

                    final var lastItemGroups = lastItem.groups();
                    if (lastItemGroups != null) {
                        lastItemGroups.add(reader.getText());
                    }
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
        private List<XmppStanza.IqQuery.TopElement> topElements;

        private Query(@NotNull final XmppStanza.IqQuery.ContentNamespace namespace) {
            this.namespace = namespace;
        }
    }
}
