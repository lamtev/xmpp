CREATE TABLE IF NOT EXISTS "user"
(
    id             SERIAL PRIMARY KEY,
    jid_local_part VARCHAR(32) NOT NULL UNIQUE,
    password       VARCHAR(32) NOT NULL,
    first_name     VARCHAR(32),
    last_name      VARCHAR(32)
);

CREATE TABLE IF NOT EXISTS roster_contact
(
    user_id      INTEGER    NOT NULL,
    contact_id   INTEGER    NOT NULL,
    name         VARCHAR(32),
    subscription VARCHAR(6) NOT NULL,

    CONSTRAINT roster_contact_pk PRIMARY KEY (user_id, contact_id),
    FOREIGN KEY (user_id) REFERENCES "user" (id),
    FOREIGN KEY (contact_id) REFERENCES "user" (id)
);

CREATE TABLE IF NOT EXISTS message
(
    id                       SERIAL PRIMARY KEY,
    sender_id                INTEGER NOT NULL,
    recipient_id             INTEGER NOT NULL,
    text                     VARCHAR NOT NULL,
    time_interval_since_1970 DOUBLE PRECISION,
    is_delivered             BOOLEAN NOT NULL,

    FOREIGN KEY (sender_id) REFERENCES "user" (id),
    FOREIGN KEY (recipient_id) REFERENCES "user" (id)
);
