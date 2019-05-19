package com.lamtev.xmpp.db;

import com.lamtev.xmpp.db.model.Contact;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class RosterStorage {
    @NotNull
    private final Connection connection;

    RosterStorage(@NotNull final Connection connection) {
        this.connection = connection;
    }

    @NotNull
    public List<Contact> contactsForUserWithJidLocalPart(@NotNull final String jidLocalPart) {
        final var contacts = new ArrayList<Contact>();

        try (final var statement = connection.createStatement()) {
            final var query = String.format(
                    "SELECT u.jid_local_part, rc.name, rc.subscription " +
                            "FROM roster_contact rc " +
                            "         JOIN \"user\" u " +
                            "              ON rc.contact_id = u.id " +
                            "WHERE user_id = (SELECT id " +
                            "                 FROM \"user\" " +
                            "                 WHERE jid_local_part = '%s' " +
                            "                 LIMIT 1)", jidLocalPart
            );
            statement.execute(query);

            final var result = statement.getResultSet();
            while (result != null && result.next()) {
                contacts.add(new Contact(result.getString(1), result.getString(2), result.getString(3)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contacts;
    }

    public boolean addContactToUserWithJidLocalPart(@NotNull final String jidLocalPart, @NotNull final Contact contact) {
        try (final var statement = connection.createStatement()) {
            final var query = String.format(
                    "INSERT INTO roster_contact " +
                            "VALUES ((SELECT id FROM \"user\" WHERE jid_local_part = '%s' LIMIT 1), " +
                            "        (SELECT id FROM \"user\" WHERE jid_local_part = '%s' LIMIT 1), " +
                            "        '%s', " +
                            "        '%s')", jidLocalPart, contact.jidLocalPart, contact.name, contact.subscription
            );
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();

            return false;
        }
        return true;
    }
}
