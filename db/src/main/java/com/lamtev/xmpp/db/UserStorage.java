package com.lamtev.xmpp.db;

import com.lamtev.xmpp.db.model.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;

public final class UserStorage {
    @NotNull
    private final Connection connection;

    UserStorage(@NotNull final Connection connection) {
        this.connection = connection;
    }

    public boolean isPresent(@NotNull final String jidLocalPart, @NotNull final String password) {
        try (final var statement = connection.createStatement()) {
            final var query = String.format(
                    "SELECT count(*) " +
                    "FROM \"user\" " +
                    "WHERE jid_local_part = '%s' AND password = '%s'", jidLocalPart, password);
            statement.execute(query);
            final var result = statement.getResultSet();
            if (result != null && result.next()) {
                return result.getInt(1) == 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Nullable
    public User userForJidLocalPart(@NotNull final String jidLocalPart, @NotNull final String password) {
        try (final var statement = connection.createStatement()) {
            final var query = String.format(
                    "SELECT first_name, last_name " +
                    "FROM \"user\" " +
                    "WHERE jid_local_part = '%s' AND password = '%s'", jidLocalPart, password);
            statement.execute(query);

            final var result = statement.getResultSet();
            if (result != null && result.next()) {
                return new User(jidLocalPart, result.getString(1), result.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
