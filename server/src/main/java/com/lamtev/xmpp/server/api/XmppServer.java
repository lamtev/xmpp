package com.lamtev.xmpp.server.api;

import com.lamtev.xmpp.core.io.XmppExchange;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 */
public interface XmppServer {
    /**
     * @param mode
     * @param port
     * @param threadPool
     * @return
     */
    @NotNull
    static XmppServer of(@NotNull final Mode mode, final int port, @NotNull final ExecutorService threadPool) {
        switch (mode) {
            case BLOCKING:
                return new BlockingXmppServer(port, threadPool);
            case NONBLOCKING:
            default:
                throw new RuntimeException("Not implemented yet");
        }
    }

    /**
     * @param mode
     * @param port
     * @param nThreads
     * @return
     */
    @NotNull
    static XmppServer of(@NotNull final Mode mode, final int port, int nThreads) {
        return XmppServer.of(mode, port, Executors.newFixedThreadPool(nThreads));
    }

    /**
     * @param handler
     */
    void setHandler(@NotNull final Handler handler);

    /**
     *
     */
    void start();

    /**
     *
     */
    void stop();

    /**
     *
     */
    enum Mode {
        BLOCKING,
        NONBLOCKING,
    }

    /**
     *
     */
    interface Handler {
        /**
         * @param exchange
         */
        void handle(@NotNull final XmppExchange exchange);
    }
}
