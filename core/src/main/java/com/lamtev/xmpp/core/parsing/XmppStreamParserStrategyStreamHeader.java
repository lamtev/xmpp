package com.lamtev.xmpp.core.parsing;

import com.lamtev.xmpp.core.XmppStreamHeader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.stream.XMLStreamReader;

final class XmppStreamParserStrategyStreamHeader extends XmppStreamParserAbstractStrategy {
    @Nullable
    private XmppStreamHeader streamHeader;
    @Nullable
    private String from;
    @Nullable
    private String to;
    @Nullable
    private String id;
    private float version;
    @Nullable
    private XmppStreamHeader.ContentNamespace contentNamespace;
    private boolean hasStreamNamespace = false;

    XmppStreamParserStrategyStreamHeader(@NotNull final XMLStreamReader reader) {
        super(reader);
    }

    @Override
    public void startElementReached(@NotNull final String name) {
        final int namespaceCount = reader.getNamespaceCount();
        for (int i = 0; i < namespaceCount; ++i) {
            final var namespacePrefix = reader.getNamespacePrefix(i);
            final var namespaceURI = reader.getNamespaceURI(i);
            if (namespacePrefix == null && XmppStreamHeader.ContentNamespace.isContentNamespace(namespaceURI)) {
                contentNamespace = XmppStreamHeader.ContentNamespace.of(namespaceURI);
            } else if ("stream".equals(namespacePrefix) && XmppStreamHeader.STREAM_NAMESPACE.equals(namespaceURI)) {
                hasStreamNamespace = true;
            }
        }

        //TODO: check namespaces
        //4.9.3.10.  invalid-namespace
        //https://xmpp.org/rfcs/rfc6120.html#streams-error-conditions-invalid-namespace

        if (!hasStreamNamespace || contentNamespace == null) {
            //TODO:
            errorObserver.onError(XmppStreamParser.Error.INVALID_NAMESPACE);
            return;
        }

        for (int i = 0; i < reader.getAttributeCount(); ++i) {
            switch (reader.getAttributeLocalName(i)) {
                case "from":
                    from = reader.getAttributeValue(i);
                    break;
                case "to":
                    to = reader.getAttributeValue(i);
                    break;
                case "id":
                    id = reader.getAttributeValue(i);
                    break;
                case "version":
                    version = Float.valueOf(reader.getAttributeValue(i));
                    break;
            }
        }

        if (hasStreamNamespace && contentNamespace != null) {
            System.out.println("Stream header received!!!!");
            streamHeader = new XmppStreamHeader(from, to, id, version, contentNamespace);
        }

    }

    @Override
    public void endElementReached() {

    }

    @Override
    public void charactersReached() {

    }

    @Override
    public boolean unitIsReady() {
        return streamHeader != null;
    }

    @Override
    @NotNull
    public XmppStreamHeader readyUnit() {
        if (streamHeader == null) {
            throw new IllegalStateException("");
        }

        final var res = streamHeader;
        streamHeader = null;

        System.out.println("Received Stream header parsed");
        return res;
    }
}
