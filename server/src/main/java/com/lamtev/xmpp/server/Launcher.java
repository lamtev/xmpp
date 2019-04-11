package com.lamtev.xmpp.server;

import com.lamtev.xmpp.core.io.XMPPInputStream;
import com.lamtev.xmpp.core.io.XMPPOutputStream;
import com.lamtev.xmpp.server.api.XMPPServer;
import org.jetbrains.annotations.NotNull;

public class Launcher {
    public static void main(String[] args) {
        final var server = XMPPServer.of(XMPPServer.Mode.BLOCKING, 12345, Runtime.getRuntime().availableProcessors());
        server.setHandler(new XMPPServer.Handler() {
            @Override
            public void handle(@NotNull XMPPInputStream initialStream, @NotNull XMPPOutputStream responseStream) {

            }

            { System.out.println("Yahoo!"); }
        });
        server.start();
    }
}
