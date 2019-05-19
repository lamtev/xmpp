package com.lamtev.xmpp.server.api;

import com.lamtev.xmpp.core.io.XmppExchange;
import com.lamtev.xmpp.core.io.XmppIOException;
import com.lamtev.xmpp.core.io.XmppInputStream;
import com.lamtev.xmpp.core.io.XmppOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;


final class BlockingXmppServer implements XmppServer {
    private final int port;
    @NotNull
    private final ExecutorService threadPool;
    @NotNull
    private final Set<XmppExchange> exchanges = ConcurrentHashMap.newKeySet();
    @NotNull
    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    @Nullable
    private Handler handler;

    BlockingXmppServer(final int port, @NotNull final ExecutorService threadPool) {
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
                    try (final var initialStream = new XmppInputStream(socket.getInputStream(), "UTF-8");
                         final var responseStream = new XmppOutputStream(socket.getOutputStream(), "UTF-8")) {
                        final var exchange = new XmppExchange(initialStream, responseStream);
                        exchanges.add(exchange);

                        initialStream.setHandler(() -> {
                            if (handler != null) {
                                handler.handle(exchange);
                            }
                        });

                        initialStream.open();

                        System.out.println("Processing is over!");
                        System.out.println();
                    } catch (XmppIOException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                        System.out.println(throwable.getMessage());
                        System.out.println();
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
