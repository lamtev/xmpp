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
        (SELECT id FROM "user" WHERE jid_local_part = 'd' LIMIT 1),
        'Adm',
        'both');

DELETE FROM roster_contact
WHERE user_id = 1;

SELECT *
FROM roster_contact;

SELECT *
FROM "user";

SELECT u.jid_local_part, rc.name, rc.subscription
FROM roster_contact rc
         JOIN "user" u
              ON rc.contact_id = u.id
WHERE user_id = (SELECT id
                 FROM "user"
                 WHERE jid_local_part = 'admin'
                 LIMIT 1)
