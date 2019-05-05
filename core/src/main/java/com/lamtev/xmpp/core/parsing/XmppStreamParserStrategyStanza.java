package com.lamtev.xmpp.core.parsing;

import com.lamtev.xmpp.core.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.stream.XMLStreamReader;

final class XmppStreamParserStrategyStanza implements XmppStreamParserStrategy {
    @NotNull
    private final XMLStreamReader reader;

    @Nullable
    private XmppStanza.Kind kind;
    @Nullable
    private String id;
    @Nullable
    private XmppStanza.TypeAttribute type;
    @Nullable
    private String resource;

    private boolean waitingForResource = false;

    private int openingTagCount = 0;
    private int closingTagCount = 0;

    @Nullable
    private XmppStanza stanza;

    XmppStreamParserStrategyStanza(@NotNull final XMLStreamReader reader) {
        this.reader = reader;
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
                stanza = new XmppStanza(kind, id, type, new XmppStanza.IqStanzaBind(resource, null));
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
        }
    }

    @Override
    public boolean unitIsReady() {
        return stanza != null;
    }

    @Override
    public @NotNull XmppUnit readyUnit() {
        return stanza;
    }

    @Override
    public void setErrorObserver(@NotNull ErrorObserver observer) {

    }
}
