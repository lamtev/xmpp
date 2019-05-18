package com.lamtev.xmpp.db;

import com.typesafe.config.Config;
import org.jetbrains.annotations.NotNull;

import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBStorage {
    @NotNull
    private final UserStorage users;

    public DBStorage(@NotNull final Config config) throws DBStorageConnectionException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        final var url = config.getString("endpoint.url");
        final var user = config.getString("endpoint.user");
        final var password = config.getString("endpoint.password");

        try {
            final var connection = DriverManager.getConnection(url, user, password);

            users = new UserStorage(connection);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBStorageConnectionException(e);
        }
    }

    //        final var config = ConfigFactory.load("endpoint.conf");

    @NotNull
    public UserStorage users() {
        return users;
    }
}
