package com.lamtev.xmpp.core.parsing;

import com.lamtev.xmpp.core.XmppStanza;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.stream.XMLStreamReader;

abstract class XmppStreamParserStrategyStanza extends XmppStreamParserAbstractStrategy {
    int tagCount = 0;
    @Nullable
    XmppStanza.Kind kind;
    @Nullable
    String id;
    @Nullable
    XmppStanza.TypeAttribute type;
    @Nullable

    XmppStreamParserStrategyStanza(@NotNull final XMLStreamReader reader) {
        super(reader);
    }

    @Override
    public void startElementReached(@NotNull final String name) {
        if (tagCount == 0) {
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
        }
        ++tagCount;
    }

    @Override
    public void endElementReached() {
        --tagCount;
    }
}
