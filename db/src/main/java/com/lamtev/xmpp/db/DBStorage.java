package com.lamtev.xmpp.db;

import com.typesafe.config.Config;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBStorage implements Closeable {
    @NotNull
    private final Connection connection;
    @NotNull
    private final UserStorage users;

    public DBStorage(@NotNull final Config config) throws DBStorageConnectionException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        final var url = config.getString("url");
        final var user = config.getString("user");
        final var password = config.getString("password");

        try {
            connection = DriverManager.getConnection(url, user, password);

            users = new UserStorage(connection);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBStorageConnectionException(e);
        }
    }

    @NotNull
    public UserStorage users() {
        return users;
    }

    @Override
    public void close() throws IOException {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
