CREATE TABLE courier
(
    id         UUID PRIMARY KEY,

    name       VARCHAR(255) NOT NULL,
    speed      INTEGER      NOT NULL,

    location_x INTEGER      NOT NULL,
    location_y INTEGER      NOT NULL,

    CONSTRAINT courier_speed_chk CHECK (speed > 0)
);

CREATE INDEX courier_name_idx ON courier (name);