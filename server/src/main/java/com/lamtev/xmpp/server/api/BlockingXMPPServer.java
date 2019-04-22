package com.lamtev.xmpp.server.api;

import com.lamtev.xmpp.core.io.XMPPExchange;
import com.lamtev.xmpp.core.io.XMPPIOException;
import com.lamtev.xmpp.core.io.XMPPInputStream;
import com.lamtev.xmpp.core.io.XMPPOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;


final class BlockingXMPPServer implements XMPPServer {
    private final int port;
    @NotNull
    private final ExecutorService threadPool;
    @NotNull
    private final Set<XMPPExchange> exchanges = ConcurrentHashMap.newKeySet();
    @NotNull
    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    @Nullable
    private Handler handler;

    BlockingXMPPServer(final int port, @NotNull final ExecutorService threadPool) {
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
            while (isRunning.get()) {
                final var socket = serverSocket.accept();
                threadPool.submit(() -> {
                    try (final var initialStream = new XMPPInputStream(socket.getInputStream(), "UTF-8");
                         final var responseStream = new XMPPOutputStream(socket.getOutputStream(), "UTF-8")) {
                        final var exchange = new XMPPExchange(initialStream, responseStream);
                        exchanges.add(exchange);

                        initialStream.setHandler(() -> {
                            if (handler != null) {
                                handler.handle(exchange);
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
        isRunning.set(false);

        exchanges.removeIf(exchange -> {
            try {
                exchange.initialStream().close();
                exchange.responseStream().close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        });
    }
}
