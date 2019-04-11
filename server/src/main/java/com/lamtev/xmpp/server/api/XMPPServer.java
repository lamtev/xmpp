package com.lamtev.xmpp.server.api;

import com.lamtev.xmpp.core.io.XMPPInputStream;
import com.lamtev.xmpp.core.io.XMPPOutputStream;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 */
public interface XMPPServer {

    /**
     * @param mode
     * @param port
     * @param threadPool
     * @return
     */
    @NotNull
    static XMPPServer of(@NotNull final Mode mode, int port, @NotNull final ExecutorService threadPool) {
        switch (mode) {
            case BLOCKING:
                return new BlockingXMPPServer(port, threadPool);
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
    static XMPPServer of(@NotNull final Mode mode, int port, int nThreads) {
        return XMPPServer.of(mode, port, Executors.newFixedThreadPool(nThreads));
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
         * @param initialStream
         * @param responseStream
         */
        void handle(@NotNull final XMPPInputStream initialStream, @NotNull final XMPPOutputStream responseStream);
    }

}
