package com.lamtev.xmpp.core.parsing;

import com.lamtev.xmpp.core.XmppStanza;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamReader;

final class XmppStreamParserStrategyStanzaPresence extends XmppStreamParserStrategyStanza {
    XmppStreamParserStrategyStanzaPresence(@NotNull final XMLStreamReader reader) {
        super(reader);
    }

    @Override
    public void startElementReached(@NotNull final String name) {
        super.startElementReached(name);

        if (openingTagCount == 2) {
            stanza = new XmppStanza(kind, id, type, new XmppStanza.UnsupportedElement(reader.getLocalName()));
        }
    }

    @Override
    public void endElementReached() {
        super.endElementReached();
    }

    @Override
    public void charactersReached() {

    }

    @Override
    void resetState() {
    }
}
