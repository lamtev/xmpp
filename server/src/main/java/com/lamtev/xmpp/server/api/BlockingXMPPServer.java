package com.lamtev.xmpp.server.api;

import com.lamtev.xmpp.core.io.XMPPIOException;
import com.lamtev.xmpp.core.io.XMPPInputStream;
import com.lamtev.xmpp.core.io.XMPPOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;


final class BlockingXMPPServer implements XMPPServer {

    private final int port;
    @NotNull
    private final ExecutorService threadPool;
    @Nullable
    private Handler handler;

    BlockingXMPPServer(int port, @NotNull final ExecutorService threadPool) {
        this.port = port;
        this.threadPool = threadPool;
    }

    @Override
    public void setHandler(@NotNull final Handler handler) {
        this.handler = handler;
    }

    @Override
    public void start() {
        try (final var serverSocket = new ServerSocket(port)) {
            while (true) {
                final var socket = serverSocket.accept();
                threadPool.submit(() -> {
                    try (final var initialStream = new XMPPInputStream(socket.getInputStream(), "");
                         final var responseStream = new XMPPOutputStream(socket.getOutputStream())) {
                        initialStream.setHandler(() -> {
                            if (handler != null) {
                                handler.handle(initialStream, responseStream);
                            }
                        });
                        initialStream.open();
                    } catch (final XMPPIOException e) {
                        e.printStackTrace();
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {

    }

}
