package com.lamtev.xmpp.db;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

final class DBStorageConnectionException extends IOException {
    DBStorageConnectionException(@NotNull final Throwable th) {
        super(th);
    }
}
