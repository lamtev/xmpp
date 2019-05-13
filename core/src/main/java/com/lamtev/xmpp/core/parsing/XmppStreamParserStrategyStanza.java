package com.lamtev.xmpp.core.parsing;

import com.lamtev.xmpp.core.XmppStanza;
import com.lamtev.xmpp.core.XmppUnit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.stream.XMLStreamReader;

abstract class XmppStreamParserStrategyStanza extends XmppStreamParserAbstractStrategy {
    int openingTagCount = 0;
    int closingTagCount = 0;
    @Nullable
    XmppStanza.Kind kind;
    @Nullable
    String id;
    @Nullable
    XmppStanza.TypeAttribute type;
    @Nullable
    String from;
    @Nullable
    String to;
    @Nullable
    String lang;
    @Nullable
    XmppStanza stanza;

    XmppStreamParserStrategyStanza(@NotNull final XMLStreamReader reader) {
        super(reader);
    }

    @Override
    public void startElementReached(@NotNull final String name) {
        if (tagCountsAreSame()) {
            kind = XmppStanza.Kind.of(name);

            for (int i = 0; i < reader.getAttributeCount(); ++i) {
                switch (reader.getAttributeLocalName(i)) {
                    case "id":
                        id = reader.getAttributeValue(i);
                        break;
                    case "type":
                        type = XmppStanza.TypeAttribute.of(kind, reader.getAttributeValue(i));
                        break;
                    case "from":
                        from = reader.getAttributeValue(i);
                        break;
                    case "to":
                        to = reader.getAttributeValue(i);
                        break;
                    case "lang":
                        //TODO: check that that works
                        lang = reader.getAttributeValue(i);
                        break;
                }
            }
        }
        ++openingTagCount;
    }

    @Override
    public void endElementReached() {
        ++closingTagCount;
    }

    @Override
    public final boolean unitIsReady() {
        return stanza != null;
    }

    @Override
    @NotNull
    public final XmppUnit readyUnit() {
        if (stanza == null) {
            throw new IllegalStateException("");
        }

        final var stanza = this.stanza;
        resetState();

        return stanza;
    }

    @Override
    void resetState() {
        openingTagCount = closingTagCount = 0;
        kind = null;
        type = null;
        id = from = to = lang = null;
        stanza = null;
    }

    final boolean tagCountsAreSame() {
        return openingTagCount == closingTagCount;
    }
}
