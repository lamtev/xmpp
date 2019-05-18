CREATE TABLE IF NOT EXISTS "user"
(
    id             SERIAL PRIMARY KEY,
    jid_local_part VARCHAR(32) NOT NULL UNIQUE,
    password       VARCHAR(32) NOT NULL,
    first_name     VARCHAR(32),
    last_name      VARCHAR(32)
);
