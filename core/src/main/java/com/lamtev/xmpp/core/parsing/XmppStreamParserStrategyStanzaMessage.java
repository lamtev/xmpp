package com.lamtev.xmpp.core.parsing;

import com.lamtev.xmpp.core.XmppStanza;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.stream.XMLStreamReader;

final class XmppStreamParserStrategyStanzaMessage extends XmppStreamParserStrategyStanza {
    private boolean waitingForBody = false;
    @Nullable
    private XmppStanza.MessageBody body;

    XmppStreamParserStrategyStanzaMessage(@NotNull final XMLStreamReader reader) {
        super(reader);
    }

    @Override
    public void startElementReached(@NotNull final String name) {
        super.startElementReached(name);

        if (openingTagCount > 0) {
            if ("body".equals(name)) {
                waitingForBody = true;
            }
        }
    }

    @Override
    public void endElementReached() {
        super.endElementReached();

        if (tagCountsAreSame()) {
            if (body != null) {
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
                if (from == null) {
                    //TODO error
                    return;
                }
                if (to == null) {
                    //TODO error
                    return;
                }

                if (lang == null) {
                    //TODO error
                    return;
                }

                stanza = new XmppStanza(kind, to, from, id, type, lang, body);
            } else {
                //TODO error
            }
        }
    }

    @Override
    public void charactersReached() {
        if (waitingForBody) {
            waitingForBody = false;
            body = new XmppStanza.MessageBody(reader.getText());
        }
    }

}
