package com.lamtev.xmpp.core.parsing;

import com.lamtev.xmpp.core.XMPPSASLAuth;
import com.lamtev.xmpp.core.XMPPStreamFeatures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.stream.XMLStreamReader;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

class XMPPStreamParserStrategySASLNegotiation implements XMPPStreamParserStrategy {
    @NotNull
    private final XMLStreamReader reader;
    @NotNull
    private ErrorObserver errorObserver;
    @Nullable
    private XMPPStreamFeatures.Type.SASLMechanism authMechanism;
    @Nullable
    private String authBody;
    @Nullable
    private XMPPSASLAuth auth;

    XMPPStreamParserStrategySASLNegotiation(@NotNull final XMLStreamReader reader) {
        this.reader = reader;
    }

    @Override
    public void startElementReached() {
        final var elementName = reader.getLocalName();
        final var namespaceURI = reader.getNamespaceURI(0);
        if (!XMPPStreamFeatures.Type.SASL.toString().equals(namespaceURI)) {
            //TODO failure

            return;
        }

        switch (elementName) {
            case "auth":
                for (int idx = 0; idx < reader.getAttributeCount(); idx++) {
                    switch (reader.getAttributeLocalName(idx)) {
                        case "mechanism":
                            final var mechanism = reader.getAttributeValue(idx);
                            if (XMPPStreamFeatures.Type.SASLMechanism.isSupported(mechanism)) {
                                authMechanism = XMPPStreamFeatures.Type.SASLMechanism.valueOf(mechanism);
                            } else {
                                errorObserver.onError(XMPPStreamParser.Error.SASL_INVALID_MECHANISM);
                                return;
                            }
                            break;
                        default:
                            errorObserver.onError(XMPPStreamParser.Error.SASL_MALFORMED_REQUEST);
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

        auth = new XMPPSASLAuth(authMechanism, authBody);
        final var x = new String(Base64.getDecoder().decode(authBody.getBytes(UTF_8)));
        final var logPass = x.split("\0");
        System.out.println(logPass[1]);
        System.out.println(logPass[2]);
        System.out.println(x);
        for (final var xe : x.toCharArray()) {
            System.out.println((int) xe);
        }
    }

    @Override
    public void charactersReached() {
        authBody = reader.getText();
    }

    @Override
    public boolean unitIsReady() {
        return authBody != null;
    }

    @Override
    @NotNull
    public XMPPSASLAuth readyUnit() {
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
