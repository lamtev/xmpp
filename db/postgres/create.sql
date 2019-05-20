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

INSERT INTO roster_contact
VALUES (1, 2, 'Anton', 'both'),
       (1, 3, 'Steve', 'from');

INSERT INTO roster_contact
VALUES ((SELECT id FROM "user" WHERE jid_local_part = 'admin' LIMIT 1),
        (SELECT id FROM "user" WHERE jid_local_part = 'steve.jobs' LIMIT 1),
        'Adm',
        'both')
ON CONFLICT ON CONSTRAINT roster_contact_pk
    DO UPDATE
    SET name         = 'Adm',
        subscription = 'to';

DELETE
FROM roster_contact
WHERE user_id = (SELECT id FROM "user" WHERE jid_local_part = 'admin' LIMIT 1)
  AND contact_id = (SELECT id FROM "user" WHERE jid_local_part = 'root' LIMIT 1);

DELETE
FROM roster_contact
WHERE contact_id = 4;

SELECT *
FROM roster_contact;

SELECT *
FROM "user";


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

INSERT INTO message (sender_id, recipient_id, text, time_interval_since_1970, is_delivered)
VALUES ((SELECT id FROM "user" WHERE jid_local_part = 'anton' LIMIT 1),
        (SELECT id FROM "user" WHERE jid_local_part = 'root' LIMIT 1),
        '',
        1.0,
        FALSE);

SELECT *
FROM message;

SELECT (SELECT jid_local_part FROM "user" WHERE "user".id = message.sender_id) AS sender_id,
       'steve.jobs'                                                            AS recipient_id,
       text,
       time_interval_since_1970,
       is_delivered
FROM message
WHERE recipient_id = (SELECT id FROM "user" WHERE jid_local_part = 'steve.jobs')
  AND is_delivered = FALSE
ORDER BY time_interval_since_1970