package com.lamtev.xmpp.core.parsing;

import com.lamtev.xmpp.core.XmppSaslAuth;
import com.lamtev.xmpp.core.XmppStreamFeatures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.stream.XMLStreamReader;

final class XMPPStreamParserStrategySASLNegotiation implements XMPPStreamParserStrategy {
    @NotNull
    private final XMLStreamReader reader;
    @NotNull
    private ErrorObserver errorObserver;
    @Nullable
    private XmppStreamFeatures.Type.SASLMechanism authMechanism;
    @Nullable
    private String authBody;
    @Nullable
    private XmppSaslAuth auth;

    XMPPStreamParserStrategySASLNegotiation(@NotNull final XMLStreamReader reader) {
        this.reader = reader;
    }

    @Override
    public void startElementReached(@NotNull final String name) {
        final var namespaceURI = reader.getNamespaceURI(0);
        if (!XmppStreamFeatures.Type.SASL.toString().equals(namespaceURI)) {
            //TODO failure

            return;
        }

        switch (name) {
            case "auth":
                for (int idx = 0; idx < reader.getAttributeCount(); idx++) {
                    switch (reader.getAttributeLocalName(idx)) {
                        case "mechanism":
                            final var mechanism = reader.getAttributeValue(idx);
                            if (XmppStreamFeatures.Type.SASLMechanism.isSupported(mechanism)) {
                                authMechanism = XmppStreamFeatures.Type.SASLMechanism.valueOf(mechanism);
                            } else {
                                errorObserver.onError(XmppStreamParser.Error.SASL_INVALID_MECHANISM);
                                return;
                            }
                            break;
                        default:
                            errorObserver.onError(XmppStreamParser.Error.SASL_MALFORMED_REQUEST);
                            return;
                    }
                }
                break;
        }
    }

    @Override
    public void endElementReached() {
        if (authMechanism == null || authBody == null) {
            throw new IllegalStateException();
        }
        System.out.println("auth received");
        auth = new XmppSaslAuth(authMechanism, authBody);
    }

    @Override
    public void charactersReached() {
        authBody = reader.getText();
        System.out.println(authBody);
    }

    @Override
    public boolean unitIsReady() {
        return authBody != null;
    }

    @Override
    @NotNull
    public XmppSaslAuth readyUnit() {
        if (auth == null) {
            throw new IllegalStateException();
        }

        final var res = auth;
        auth = null;

        return res;
    }

    @Override
    public void setErrorObserver(@NotNull ErrorObserver observer) {
        this.errorObserver = observer;
    }
}
