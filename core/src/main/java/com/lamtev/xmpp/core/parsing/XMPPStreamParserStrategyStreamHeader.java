package com.lamtev.xmpp.core.parsing;

import com.lamtev.xmpp.core.XMPPStreamHeader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.stream.XMLStreamReader;

final class XMPPStreamParserStrategyStreamHeader implements XMPPStreamParserStrategy {
    @NotNull
    private final XMLStreamReader reader;
    @Nullable
    private XMPPStreamHeader streamHeader;

    @Nullable
    private String from;
    @Nullable
    private String to;
    @Nullable
    private String id;
    private float version;
    @Nullable
    private XMPPStreamHeader.ContentNamespace contentNamespace;
    private boolean hasStreamNamespace = false;
    @NotNull
    private ErrorObserver errorObserver;

    XMPPStreamParserStrategyStreamHeader(@NotNull final XMLStreamReader reader) {
        this.reader = reader;
    }

    @Override
    public void startElementReached() {
        final int namespaceCount = reader.getNamespaceCount();
        System.out.println(namespaceCount + " namespaces");
        for (int i = 0; i < namespaceCount; ++i) {
            final var namespacePrefix = reader.getNamespacePrefix(i);
            final var namespaceURI = reader.getNamespaceURI(i);
            if (namespacePrefix == null && XMPPStreamHeader.ContentNamespace.isContentNamespace(namespaceURI)) {
                contentNamespace = XMPPStreamHeader.ContentNamespace.of(namespaceURI);
            } else if ("stream".equals(namespacePrefix) && XMPPStreamHeader.STREAM_NAMESPACE.equals(namespaceURI)) {
                hasStreamNamespace = true;
            }
            System.out.println("Namespace " + i + ": " + reader.getNamespacePrefix(i) + ":" + reader.getNamespaceURI(i));
        }

        //TODO: check namespaces
        //4.9.3.10.  invalid-namespace
        //https://xmpp.org/rfcs/rfc6120.html#streams-error-conditions-invalid-namespace

        if (!hasStreamNamespace || contentNamespace == null) {
            //TODO:
            errorObserver.onError(XMPPStreamParser.Error.INVALID_NAMESPACE);
            return;
        }

        System.out.println("Tag=<" + reader.getName().getPrefix() + ":" + reader.getName().getLocalPart() + ">");
        System.out.println(reader.getAttributeCount() + " attributes:");
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
            System.out.println("attribute " + i + ": " + reader.getAttributeName(i).getLocalPart() + " = " + reader.getAttributeValue(i));
        }

        if (hasStreamNamespace && contentNamespace != null) {
            streamHeader = new XMPPStreamHeader(from, to, id, version, contentNamespace);
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
    public XMPPStreamHeader readyUnit() {
        if (streamHeader == null) {
            throw new IllegalStateException("");
        }

        final var res = streamHeader;
        streamHeader = null;

        return res;
    }

    @Override
    public void setErrorObserver(@NotNull final ErrorObserver observer) {
        errorObserver = observer;
    }
}
