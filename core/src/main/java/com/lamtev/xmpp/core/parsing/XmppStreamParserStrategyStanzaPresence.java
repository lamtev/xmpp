package com.lamtev.xmpp.core.parsing;

import com.lamtev.xmpp.core.XmppStanza;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.stream.XMLStreamReader;

final class XmppStreamParserStrategyStanzaPresence extends XmppStreamParserStrategyStanza {
    @Nullable
    private XmppStanza st;

    XmppStreamParserStrategyStanzaPresence(@NotNull final XMLStreamReader reader) {
        super(reader);
    }

    @Override
    public void startElementReached(@NotNull final String name) {
        super.startElementReached(name);

        if (openingTagCount == 1) {
            st = new XmppStanza(kind, to, from, id, type, lang, XmppStanza.PresenceEmpty.instance());
        }
    }

    @Override
    public void endElementReached() {
        super.endElementReached();

        if (tagCountsAreSame() && st != null) {
            stanza = st;
        }
    }

    @Override
    void resetState() {
        super.resetState();
        st = null;
    }

    @Override
    public void charactersReached() {

    }
}
