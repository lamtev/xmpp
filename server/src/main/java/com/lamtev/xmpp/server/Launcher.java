package com.lamtev.xmpp.server;

import com.lamtev.xmpp.core.io.XmppExchange;
import com.lamtev.xmpp.server.api.XMPPServer;
import org.jetbrains.annotations.NotNull;

public class Launcher {
    public static void main(String[] args) {
        final var server = XMPPServer.of(XMPPServer.Mode.BLOCKING, 12345, Runtime.getRuntime().availableProcessors());
        server.setHandler(new XMPPServer.Handler() {
            @Override
            public void handle(@NotNull XmppExchange exchange) {}
            { System.out.println("Yahoo!"); }
        });
        server.start();
    }
}
