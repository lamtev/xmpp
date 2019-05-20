package com.lamtev.xmpp.db;

import com.lamtev.xmpp.db.model.Message;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class MessageStorage {
    @NotNull
    private final Connection connection;

    public MessageStorage(@NotNull final Connection connection) {
        this.connection = connection;
    }

    public boolean add(@NotNull final Message message) {
        try (final var statement = connection.createStatement()) {
            final var query = String.format(
                    "INSERT INTO message (sender_id, recipient_id, text, time_interval_since_1970, is_delivered)\n" +
                            "VALUES ((SELECT id FROM \"user\" WHERE jid_local_part = '%s' LIMIT 1),\n" +
                            "        (SELECT id FROM \"user\" WHERE jid_local_part = '%s' LIMIT 1),\n" +
                            "        '%s',\n" +
                            "        %f,\n" +
                            "        %b)", message.senderJidLocalPart, message.recipientJidLocalPart, message.text, message.timestamp, message.isDelivered
            );
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<Message> incomingNotDeliveredMessagesForUserWithJidLocalPart(@NotNull final String jidLocalPart) {
        final var messages = new ArrayList<Message>();

        try (final var statement = connection.createStatement()) {
            final var query = String.format(
                    "SELECT (SELECT jid_local_part FROM \"user\" WHERE \"user\".id = message.sender_id) AS sender_jid_local_part, " +
                            "       '%s'                                                            AS recipient_jid_local_part, " +
                            "       text, " +
                            "       time_interval_since_1970, " +
                            "       is_delivered " +
                            "FROM message " +
                            "WHERE recipient_id = (SELECT id FROM \"user\" WHERE jid_local_part = '%s') " +
                            "  AND is_delivered = FALSE " +
                            "ORDER BY time_interval_since_1970", jidLocalPart, jidLocalPart
            );
            statement.execute(query);
            final var result = statement.getResultSet();
            while (result != null && result.next()) {
                messages.add(new Message(result.getString(1), result.getString(2), result.getString(3), result.getDouble(4)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
    }
}
